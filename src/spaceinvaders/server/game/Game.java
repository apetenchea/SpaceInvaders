package spaceinvaders.server.game;

import static spaceinvaders.game.EnumEntity.INVADER;
import static spaceinvaders.game.EnumEntity.PLAYER;
import static spaceinvaders.game.EnumEntity.SHIELD;
import static spaceinvaders.game.EnumEntity.INVADER_BULLET;
import static spaceinvaders.game.EnumEntity.PLAYER_BULLET;

import java.util.concurrent.Callable;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
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
  private CommandDirector director;
  private ReadWriteLock entitiesLock;
  private ServiceState state;

  /**
   * Initialize game world.
   */
  public Game(ExecutorService threadPool, List<Player> players) { 
    this.threadPool = threadPool;
    this.players = players;
    entitiesLock = new ReentrantReadWriteLock();
    state = new ServiceState(false);
    director = new CommandDirector(new ServerCommandBuilder());
    entities = GameWorld.getInstance().makeGame(makeIdsList());
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
    playGame();
    //sendQuitSignal();
    return null;
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

  private void sendPlayerNames() {
    sendToPlayers(new SetPlayerNamesCommand(makeCouplesIdName()));
  }

  private void playGame() {
    Future<Void> invaderMovementFuture = threadPool.submit(new InvadersMover());
    try {
      invaderMovementFuture.get();
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
      invaderSpeed = 500;
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
      while (true) {
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
          entitiesLock.readLock().lock();
          for (Entity invader : invaders) {
            sendToPlayers(new MoveEntityCommand(invader.getId(),
                  invader.getX() + jump,invader.getY()));
          }
          entitiesLock.readLock().unlock();
          entitiesLock.writeLock().lock();
          for (Entity invader : invaders) {
            invader.move(invader.getX() + jump,invader.getY());
          }
          entitiesLock.writeLock().unlock();
          //detectCollision(INVADER);
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
        entitiesLock.readLock().lock();
        for (Entity invader : invaders) {
          sendToPlayers(new MoveEntityCommand(invader.getId(),
                invader.getX(),invader.getY() + jump));
        }
        entitiesLock.readLock().unlock();
        entitiesLock.writeLock().lock();
        for (Entity invader : invaders) {
          invader.move(invader.getX(),invader.getY() + jump);
        }
        entitiesLock.writeLock().unlock();
        // detect colision
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
        sendToPlayers(new FlushScreenCommand());
        sendToPlayers(new GameOverCommand());
        entitiesLock.readLock().unlock();
      }
    }
  }
}
