package spaceinvaders.server.game;

import static spaceinvaders.game.EnumEntity.INVADER;
import static spaceinvaders.game.EnumEntity.PLAYER;
import static spaceinvaders.game.EnumEntity.SHIELD;
import static spaceinvaders.game.EnumEntity.INVADER_BULLET;
import static spaceinvaders.game.EnumEntity.PLAYER_BULLET;

import java.util.Random;
import java.util.concurrent.Callable;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import spaceinvaders.game.GameWorld;
import spaceinvaders.server.players.Player;
import spaceinvaders.utility.Couple;
import spaceinvaders.game.Entity;
import spaceinvaders.game.EnumEntity;
import spaceinvaders.game.GameConfig;
import spaceinvaders.command.Command;
import spaceinvaders.command.CommandDirector;
import spaceinvaders.command.CommandBuilder;
import spaceinvaders.command.server.builder.ServerCommandBuilder;
import spaceinvaders.exceptions.InterruptedServiceException;
import spaceinvaders.command.client.AddEntityCommand;
import spaceinvaders.command.client.GameOverCommand;
import spaceinvaders.command.client.SetPlayerNamesCommand;
import spaceinvaders.command.client.FlushScreenCommand;
import spaceinvaders.command.client.MoveEntityCommand;
import spaceinvaders.command.client.DestroyEntityCommand;
import spaceinvaders.utility.ServiceState;
import spaceinvaders.exceptions.ClosingSocketException;

/**
 * Game logic and physics happen here.
 */
public class Game implements Callable<Void> {
  private static final Logger LOGGER = Logger.getLogger(Game.class.getName());
  private static final int GUARD_PIXELS = 10;
  private static final int WAITING_TIME_MILLISECONDS = 1000;

  private ExecutorService threadPool;
  private List<Player> players;
  private Map<EnumEntity,List<Entity>> entities; 
  private Map<Entity,Future<Void>> bulletMap; 
  private Map<Integer,Future<Void>> playerMap;
  private ReadWriteLock entitiesLock;
  private Lock movingEntities;
  private ServiceState state;

  /**
   * Initialize game world.
   */
  public Game(ExecutorService threadPool, List<Player> players) { 
    this.threadPool = threadPool;
    this.players = players;
    entitiesLock = new ReentrantReadWriteLock();
    movingEntities = new ReentrantLock();
    state = new ServiceState(false);
    entities = GameWorld.getInstance().makeGame(makeIdsList());
    bulletMap = new ConcurrentHashMap<>();
    playerMap = new ConcurrentHashMap<>();
  }

  @Override
  public Void call() {
    state.set(true);
    sendWorld();
    sendPlayerNames();
    sendToPlayers(new FlushScreenCommand());
    try {
      Thread.sleep(WAITING_TIME_MILLISECONDS);
    } catch (InterruptedException exception) {
      LOGGER.log(Level.SEVERE,exception.toString(),exception);
    }
    Game thisGame = this;
    for (Player player : players) {
      playerMap.put(player.hashCode(),threadPool.submit(new Callable<Void>() {
        private CommandDirector director;
        @Override
        public Void call() {
          director = new CommandDirector(new ServerCommandBuilder());
          while (state.get()) {
            try {
              String data = player.pull();
              if (data != null) {
                director.makeCommand(data);
                Command command = director.getCommand();
                command.setExecutor(thisGame);
                command.execute();
              }
            } catch (Exception e) {
              if (state.get()) {
                e.printStackTrace();
              }
            }
          }
          return null;
        }
      }));
    }
    playGame();
    for (Player player : players) {
      try {
        player.close();
      } catch (ClosingSocketException exception) {
        LOGGER.log(Level.SEVERE,exception.getMessage(),exception);
      }
    }
    return null;
  }

  public void movePlayerLeft(int id) {
    try{
    int playerJump = GameWorld.getInstance().getPlayerJump();
    entitiesLock.readLock().lock();
    List<Entity> players = entities.get(PLAYER);
    for (Entity player : players) {
      if (player.getId() == id) {
        if (player.getX() - playerJump <= 32) {
          break;
        }
        entitiesLock.readLock().unlock();
        movingEntities.lock();
        entitiesLock.writeLock().lock();
        player.move(player.getX() - GameWorld.getInstance().getPlayerJump(),player.getY());
        entitiesLock.writeLock().unlock();
        sendToPlayers(new MoveEntityCommand(id,player.getX(),player.getY()));
        sendToPlayers(new FlushScreenCommand());
        movingEntities.unlock();
        return;
      }
    }
    entitiesLock.readLock().unlock();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void movePlayerRight(int id) {
    int playerJump = GameWorld.getInstance().getPlayerJump();
    int rightMargin = GameConfig.getInstance().getGameFrameWidth();
    try {
    entitiesLock.readLock().lock();
    List<Entity> players = entities.get(PLAYER);
    for (Entity player : players) {
      if (player.getId() == id) {
        if (player.getX() + playerJump >= rightMargin - 64) {
          break;
        }
        entitiesLock.readLock().unlock();
        movingEntities.lock();
        entitiesLock.writeLock().lock();
        player.move(player.getX() + playerJump,player.getY());
        entitiesLock.writeLock().unlock();
        sendToPlayers(new MoveEntityCommand(id,player.getX(),player.getY()));
        sendToPlayers(new FlushScreenCommand());
        movingEntities.unlock();
        return;
      }
    }
    entitiesLock.readLock().unlock();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void playerShoots(int id) {
    try {
    GameConfig config = GameConfig.getInstance();
    entitiesLock.readLock().lock();
    List<Entity> players = entities.get(PLAYER);
    for (Entity player : players) {
      if (player.getId() == id) {
        Entity playerBullet = new Entity(new Couple<Integer,Integer>(
              player.getX() + (config.getPlayerWidth() - config.getBulletWidth()) / 2,
              player.getY() - config.getBulletHeight())); 
        entitiesLock.readLock().unlock();
        movingEntities.lock();
        sendToPlayers(new AddEntityCommand(PLAYER_BULLET,playerBullet));
        sendToPlayers(new FlushScreenCommand());
        movingEntities.unlock();
        entitiesLock.writeLock().lock();
        entities.get(PLAYER_BULLET).add(playerBullet);
        entitiesLock.writeLock().unlock();
        Future<Void> bulletFuture = threadPool.submit(new PlayerBullet(playerBullet));
        bulletMap.put(playerBullet,bulletFuture);
        return;
      }
    }
    entitiesLock.readLock().unlock();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void sendToPlayers(Command command) {
    for (Player player : players) {
      try {
        player.push(command.toJson());
      } catch (InterruptedServiceException exception) {
        LOGGER.log(Level.SEVERE,exception.getMessage(),exception);
      }
    }
  }

  private void sendWorld() {
    for (Map.Entry<EnumEntity,List<Entity>> entry : entities.entrySet()) {
      EnumEntity type = entry.getKey();
      for (Entity entity : entry.getValue()) {
        sendToPlayers(new AddEntityCommand(type,entity));
      }
    }
  }

  private synchronized void sendPlayerNames() {
    sendToPlayers(new SetPlayerNamesCommand(makeCouplesIdName()));
  }

  private void playGame() {
    Future<Void> invaderMovementFuture = threadPool.submit(new InvadersMover());
    threadPool.submit(new InvadersBulletGenerator());
    
    try {
      invaderMovementFuture.get();
      for (Map.Entry<Entity,Future<Void>> entry : bulletMap.entrySet()) {
        Future<Void> future = entry.getValue();
        future.cancel(true);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private List<Integer> makeIdsList() {
    List<Integer> ids = new ArrayList<>(players.size());
    for (Player player : players) {
      ids.add(player.hashCode());
    }
    return ids;
  }

  private List<Couple<Integer,String>> makeCouplesIdName() {
    List<Couple<Integer,String>> couples = new ArrayList<>(players.size());
    for (Player player : players) {
      couples.add(new Couple<Integer,String>(player.hashCode(),player.getName()));
    }
    return couples;
  }

  private boolean checkCollision(Entity entityA, Couple<Integer,Integer> entityASize,
      Entity entityB, Couple<Integer,Integer> entityBSize) {
    return entityA.getX() < entityB.getX() + entityBSize.getFirst() &&
      entityA.getX() + entityASize.getFirst() > entityB.getX() &&
      entityA.getY() < entityB.getY() + entityBSize.getSecond() &&
      entityA.getY() + entityASize.getSecond() > entityB.getY();
  }

  private class InvadersMover implements Callable<Void> {
    Integer frameWidth;
    Integer invadersBottomLimit;
    Integer playerHeight;
    Integer invaderWidth;
    Integer invaderHeight;
    Integer invaderJump;
    Integer invaderSpeed;

    public InvadersMover() {
      GameConfig config = GameConfig.getInstance();
      GameWorld world = GameWorld.getInstance();
      frameWidth = config.getGameFrameWidth();
      playerHeight = config.getPlayerHeight();
      invaderWidth = config.getInvaderWidth();
      invaderHeight = config.getInvaderHeight();
      invaderJump = world.getInvaderJump();
      invadersBottomLimit = world.getPlayersOffsetY();
      //invaderSpeed = world.getInvaderSpeed();
      invaderSpeed = 1000;
    }

    @Override
    public Void call() {
      int invaderWidth = GameConfig.getInstance().getInvaderWidth();
      int invaderHeight = GameConfig.getInstance().getInvaderHeight();
      while (state.get()) {
        moveInvadersOnX(invaderJump);
        moveInvadersOnY(invaderJump);
        moveInvadersOnX(-invaderJump);
        moveInvadersOnY(invaderJump);
      }
      return null;
    }

    private void moveInvadersOnX(int jump) {
      while (state.get()) {
        int marginLeft = Integer.MAX_VALUE;
        int marginRight = Integer.MIN_VALUE;
        entitiesLock.readLock().lock();
        List<Entity> invaders = entities.get(INVADER);
        for (Entity invader : invaders) {
          marginLeft = Integer.min(marginLeft,invader.getX());
          marginRight = Integer.max(marginRight,invader.getX());
        }
        entitiesLock.readLock().unlock();
        if (marginLeft + jump > GUARD_PIXELS && marginRight + jump < frameWidth - invaderWidth - GUARD_PIXELS) {
          movingEntities.lock();
          entitiesLock.readLock().lock();
          for (Entity invader : invaders) {
            sendToPlayers(new MoveEntityCommand(invader.getId(),
                  invader.getX() + jump,invader.getY()));
          }
          entitiesLock.readLock().unlock();
          movingEntities.unlock();
          entitiesLock.writeLock().lock();
          for (Entity invader : invaders) {
            invader.move(invader.getX() + jump,invader.getY());
          }
          entitiesLock.writeLock().unlock();
          detectCollision();
          sendToPlayers(new FlushScreenCommand());
          try {
            Thread.sleep(invaderSpeed);
          } catch (InterruptedException exception) {
            LOGGER.log(Level.SEVERE,exception.toString(),exception);
          }
        } else {
          break;
        }
      }
    }

    private void moveInvadersOnY(int jump) {
      int marginDown = Integer.MIN_VALUE;
      entitiesLock.readLock().lock();
      List<Entity> invaders = entities.get(INVADER);
      for (Entity invader : invaders) {
        marginDown = Integer.max(marginDown,invader.getY());
      }
      entitiesLock.readLock().unlock();
      if (marginDown + jump < invadersBottomLimit - GUARD_PIXELS) {
        movingEntities.lock();
        entitiesLock.readLock().lock();;
        for (Entity invader : invaders) {
          sendToPlayers(new MoveEntityCommand(invader.getId(),
                invader.getX(),invader.getY() + jump));
        }
        entitiesLock.readLock().unlock();
        movingEntities.unlock();
        entitiesLock.writeLock().lock();
        for (Entity invader : invaders) {
          invader.move(invader.getX(),invader.getY() + jump);
        }
        entitiesLock.writeLock().unlock();
        detectCollision();
        sendToPlayers(new FlushScreenCommand());
        try {
          Thread.sleep(invaderSpeed);
        } catch (InterruptedException exception) {
          LOGGER.log(Level.SEVERE,exception.toString(),exception);
        }
      }
      else {
        // game over - invaders reach bottom
        state.set(false);
        entitiesLock.readLock().lock();
        for (Entity invader : invaders) {
          sendToPlayers(new MoveEntityCommand(invader.getId(),
                invader.getX(),invader.getY() + jump));
        }
        List<Entity> players = entities.get(PLAYER);
        for (Entity player : players) {
          sendToPlayers(new DestroyEntityCommand(player.getId()));
        }
        entitiesLock.readLock().unlock();
        sendToPlayers(new FlushScreenCommand());
        sendToPlayers(new GameOverCommand());
      }
    }

    private void detectCollision() {
      GameConfig config = GameConfig.getInstance();
      entitiesLock.readLock().lock();
      List<Entity> invaders = entities.get(INVADER);
      List<Entity> shields = entities.get(SHIELD);
      entitiesLock.readLock().unlock();
      entitiesLock.writeLock().lock();
      List<Entity> collidingShields = new ArrayList<>();
      for (Entity invader : invaders) {
        for (Entity shield : shields) {
          if (checkCollision(invader,config.getInvaderSize(),
                shield,config.getShieldSize())) {
            sendToPlayers(new DestroyEntityCommand(shield.getId()));
            collidingShields.add(shield);
          }
        }
      }
      for (Entity entity : collidingShields) {
        shields.remove(entity);
      }
      entitiesLock.writeLock().unlock();
    }
  }

  private class InvadersBulletGenerator implements Callable<Void> {
    Random rnd = new Random(77977);
    @Override
    public Void call() {
      try {
      GameConfig config = GameConfig.getInstance();
      int invaderWidth = config.getInvaderWidth();
      int invaderHeight = config.getInvaderWidth();
      int bulletWidth = config.getBulletWidth();
      int bulletHeight = config.getBulletHeight();

      while (state.get()) {
        entitiesLock.readLock().lock();
        List<Entity> invaders = entities.get(INVADER);
        int index = rnd.nextInt(invaders.size());
        Entity invader = invaders.get(index);
        Entity invaderBullet = new Entity(new Couple<Integer,Integer>(
              invader.getX() + (invaderWidth - bulletWidth) / 2,
              invader.getY() + invaderHeight + bulletHeight / 4)); 
        entitiesLock.readLock().unlock();
        movingEntities.lock();
        sendToPlayers(new AddEntityCommand(INVADER_BULLET,invaderBullet));
        sendToPlayers(new FlushScreenCommand());
        movingEntities.unlock();
        entitiesLock.writeLock().lock();
        entities.get(INVADER_BULLET).add(invaderBullet);
        entitiesLock.writeLock().unlock();
        Future<Void> bulletFuture = threadPool.submit(new InvaderBullet(invaderBullet));
        bulletMap.put(invaderBullet,bulletFuture);
        try {
          Thread.sleep(rnd.nextInt(2000) + 1000);
        } catch (InterruptedException exception) {
          LOGGER.log(Level.SEVERE,exception.toString(),exception);
        }
      }
      } catch (Exception e) {
        e.printStackTrace();
      }
      return null;
    }
  }

  private class InvaderBullet implements Callable<Void> {
    private Entity entity;
    private Couple<Integer,Integer> size = GameConfig.getInstance().getBulletSize();

    public InvaderBullet(Entity entity) {
      this.entity = entity; 
    }

    @Override
    public Void call() {
      try {
      GameWorld config = GameWorld.getInstance();
      int speed = config.getBulletSpeed();
      int jump = config.getBulletJump();
      while (state.get()) {
        if (detectCollision()) {
          entitiesLock.writeLock().lock();
          bulletMap.remove(entity);
          entities.get(INVADER_BULLET).remove(entity);
          entitiesLock.writeLock().unlock();
          sendToPlayers(new DestroyEntityCommand(entity.getId()));
          movingEntities.lock();
          sendToPlayers(new FlushScreenCommand());
          movingEntities.unlock();
          break;
        }
        entitiesLock.readLock().lock();
        if (entity.getY() >= GameConfig.getInstance().getGameFrameWidth() + 32) {
          sendToPlayers(new DestroyEntityCommand(entity.getId()));
          entitiesLock.readLock().unlock();
          entitiesLock.writeLock().lock();
          entities.get(INVADER_BULLET).remove(entity);
          bulletMap.remove(entity);
          entitiesLock.writeLock().unlock();
          break;
        }
        entitiesLock.readLock().unlock();
        entity.move(entity.getX(),entity.getY() + jump);
        movingEntities.lock();
        sendToPlayers(new MoveEntityCommand(entity.getId(),entity.getX(),entity.getY()));
        sendToPlayers(new FlushScreenCommand());
        movingEntities.unlock();
        try {
          Thread.sleep(speed);
        } catch (InterruptedException exception) {
          LOGGER.log(Level.SEVERE,exception.toString(),exception);
        }
      }
      } catch (Exception e) {
        e.printStackTrace();
      }
      return null;
    }

    private boolean detectCollision() {
      GameConfig config = GameConfig.getInstance();
      entitiesLock.readLock().lock();
      List<Entity> players = entities.get(PLAYER);
      List<Entity> playerBullets = entities.get(PLAYER_BULLET);
      List<Entity> shields = entities.get(SHIELD);
      for (Entity playerBullet : playerBullets) {
        if (checkCollision(entity,size,playerBullet,size)) {
          entitiesLock.readLock().unlock();
          entitiesLock.writeLock().lock();
          playerBullets.remove(playerBullet);
          entitiesLock.writeLock().unlock();
          sendToPlayers(new DestroyEntityCommand(playerBullet.getId()));
          Future<Void> future = bulletMap.get(playerBullet);
          bulletMap.remove(playerBullet);
          if (future != null) {
            future.cancel(true);
          }
          return true;
        }
      }
      for (Entity player : players) {
        if (checkCollision(entity,size,player,config.getPlayerSize())) {
          entitiesLock.readLock().unlock();
          entitiesLock.writeLock().lock();
          players.remove(player);
          entitiesLock.writeLock().unlock();
          sendToPlayers(new DestroyEntityCommand(player.getId()));
          if (players.isEmpty()) {
            state.set(false);
          }
          return true;
        }
      }
      for (Entity shield : shields) {
        if (checkCollision(entity,size,shield,config.getShieldSize())) {
          entitiesLock.readLock().unlock();
          entitiesLock.writeLock().lock();
          shields.remove(shield);
          entitiesLock.writeLock().unlock();
          sendToPlayers(new DestroyEntityCommand(shield.getId()));
          return true;
        }
      }
      entitiesLock.readLock().unlock();
      return false;
    }
  }

  private class PlayerBullet implements Callable<Void> {
    private Entity entity;
    private Couple<Integer,Integer> size = GameConfig.getInstance().getBulletSize();

    public PlayerBullet(Entity entity) {
      this.entity = entity; 
    }

    @Override
    public Void call() {
      try {
      GameWorld config = GameWorld.getInstance();
      int speed = config.getBulletSpeed();
      int jump = config.getBulletJump();
      while (state.get()) {
        if (detectCollision()) {
          entitiesLock.writeLock().lock();
          bulletMap.remove(entity);
          entities.get(PLAYER_BULLET).remove(entity);
          entitiesLock.writeLock().unlock();
          sendToPlayers(new DestroyEntityCommand(entity.getId()));
          movingEntities.lock();
          sendToPlayers(new FlushScreenCommand());
          movingEntities.unlock();
          break;
        }
        entitiesLock.readLock().lock();
        if (entity.getY() <= 32) {
          sendToPlayers(new DestroyEntityCommand(entity.getId()));
          entitiesLock.readLock().unlock();
          entitiesLock.writeLock().lock();
          entities.get(INVADER_BULLET).remove(entity);
          bulletMap.remove(entity);
          entitiesLock.writeLock().unlock();
          break;
        }
        entitiesLock.readLock().unlock();
        entity.move(entity.getX(),entity.getY() - jump);
        movingEntities.lock();
        sendToPlayers(new MoveEntityCommand(entity.getId(),entity.getX(),entity.getY()));
        sendToPlayers(new FlushScreenCommand());
        movingEntities.unlock();
        try {
          Thread.sleep(speed);
        } catch (InterruptedException exception) {
          LOGGER.log(Level.SEVERE,exception.toString(),exception);
        }
      }
      } catch (Exception e) {
        e.printStackTrace();
      }
      return null;
    }

    private boolean detectCollision() {
      GameConfig config = GameConfig.getInstance();
      entitiesLock.readLock().lock();
      List<Entity> invaders = entities.get(INVADER);
      List<Entity> invaderBullets = entities.get(INVADER_BULLET);
      for (Entity invaderBullet : invaderBullets) {
        if (checkCollision(entity,size,invaderBullet,size)) {
          entitiesLock.readLock().unlock();
          entitiesLock.writeLock().lock();
          invaderBullets.remove(invaderBullet);
          entitiesLock.writeLock().unlock();
          sendToPlayers(new DestroyEntityCommand(invaderBullet.getId()));
          Future<Void> future = bulletMap.get(invaderBullet);
          bulletMap.remove(invaderBullet);
          if (future != null) {
            future.cancel(true);
          }
          return true;
        }
      }
      for (Entity invader : invaders) {
        if (checkCollision(entity,size,invader,config.getInvaderSize())) {
          entitiesLock.readLock().unlock();
          entitiesLock.writeLock().lock();
          invaders.remove(invader);
          entitiesLock.writeLock().unlock();
          sendToPlayers(new DestroyEntityCommand(invader.getId()));
          if (invaders.isEmpty()) {
            state.set(false);
          }
          return true;
        }
      }
      entitiesLock.readLock().unlock();
      return false;
    }
  }
}
