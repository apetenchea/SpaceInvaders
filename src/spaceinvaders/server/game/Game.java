package spaceinvaders.server.game;

import static java.util.logging.Level.SEVERE;

import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.ArrayList;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Logger;
import spaceinvaders.command.Command;
import spaceinvaders.command.client.SetPlayerNamesCommand;
import spaceinvaders.command.client.StartGameCommand;
import spaceinvaders.command.client.GameOverCommand;
import spaceinvaders.game.GameConfig;
import spaceinvaders.command.client.QuitGameCommand;
import spaceinvaders.command.client.RefreshEntitiesCommand;
import spaceinvaders.command.client.FlushScreenCommand;
import spaceinvaders.command.client.IncrementScoreCommand;
import spaceinvaders.command.client.PackCommand;
import spaceinvaders.server.game.world.WorldDirector;
import spaceinvaders.server.game.world.ClassicWorldBuilder;
import spaceinvaders.server.game.world.World;
import spaceinvaders.server.player.Player;
import spaceinvaders.game.Entity;
import spaceinvaders.server.game.world.LogicEntity;
import spaceinvaders.server.game.world.PlayerBullet;
import spaceinvaders.game.EntityEnum;
import spaceinvaders.utility.Couple;
import spaceinvaders.utility.Service;
import spaceinvaders.utility.ServiceState;
import spaceinvaders.utility.AutoSwitch;

/** Game logic and physics happen here. */
public class Game implements Service<Void> {
  private static final Logger LOGGER = Logger.getLogger(Game.class.getName());
  private static final int SLEEP_BETWEEN_FRAMES_MS = 50;
  private static final int GUARD_PIXELS = 32;
  private static final boolean PREDICTABLE = false;

  private final GameConfig config = GameConfig.getInstance();
  private final List<Player> team;
  private final World world;
  private final AutoSwitch invadersMovement = new AutoSwitch(config.speed().invader().getRate()); 
  private final AutoSwitch bulletsMovement = new AutoSwitch(config.speed().bullet().getRate()); 
  private final AutoSwitch invadersShooting = new AutoSwitch(config.getInvadersShootingRate()); 
  private final Random rng;
  private final List<Service<?>> services = new ArrayList<>();
  private final List<Future<?>> future = new ArrayList<>();
  private final ReadWriteLock teamListLock = new ReentrantReadWriteLock();
  private final List<Command> udpBuffer = new ArrayList<>();
  private final List<Command> tcpBuffer = new ArrayList<>();
  private final ServiceState state = new ServiceState();
  private final ExecutorService threadPool;
  private Integer invadersMoveX = config.speed().invader().getDistance();
  private Integer invadersMoveY = config.speed().invader().getDistance();

  /**
   * Create a new game.
   *
   * @param team - players joining this game.
   * @param threadPool - pool used to create new threads.
   *
   * @throws ClassCastException - from {@link TreeMap#put()}.
   * @throws NullPointerException - if any of the arguments is {@code null}
   *     or from {@link TreeMap#put()}.
   */
  public Game(List<Player> team, ExecutorService threadPool) {
    LOGGER.info("*** Game init");
    if (team == null || threadPool == null) {
      throw new NullPointerException();
    }
    this.team = team;
    this.threadPool = threadPool;
    WorldDirector director = new WorldDirector(new ClassicWorldBuilder());
    director.makeWorld(team.size());
    world = director.getWorld();
    // TODO random exceptions
    if (PREDICTABLE) {
      rng = new Random(1103515245);
    } else {
      rng = new Random();
    }

    // Match real players with an entities.
    Iterator<LogicEntity> entityIt = world.getIterator(EntityEnum.PLAYER);
    Iterator<Player> playerIt = team.iterator();
    while (entityIt.hasNext() && playerIt.hasNext()) {
      LogicEntity entity = entityIt.next();
      Player player = playerIt.next();
      entity.getEntity().setId(player.getId());
    }
    state.set(true);
  }

  /**
   * Game loop.
   *
   * @throws ExecutionException - if an exception occurs during execution.
   * @throws InterruptedException - if the service is interrupted prior to shutdown.
   * @throws RejectedExecutionException - if the task cannot be executed.
   */
  @Override
  public Void call() throws ExecutionException, InterruptedException {
    LOGGER.info("Game started.");
    initClients();
    future.add(threadPool.submit(invadersMovement));
    future.add(threadPool.submit(bulletsMovement));
    future.add(threadPool.submit(invadersShooting));
    services.add(invadersMovement);
    services.add(bulletsMovement);
    while (state.get()) {
      try {
        for (Future<?> it : future) {
          if (it.isDone()) {
            state.set(false);
            it.get();
          }
        }
        processInput();
        update();
        List<Entity> entityList = new ArrayList<>();
        for (EntityEnum type : EntityEnum.values()) {
          Iterator<LogicEntity> it = world.getIterator(type);
          while (it.hasNext()) {
            LogicEntity entity = it.next();
            entityList.add(entity.getEntity());
          }
        }
        sendCommand(new RefreshEntitiesCommand(entityList));
        sendCommand(new FlushScreenCommand());
        for (Player player : team) {
          player.flush();
        }
        Thread.sleep(SLEEP_BETWEEN_FRAMES_MS);
      } catch (CancellationException | InterruptedException exception) {
        if (state.get()) {
          state.set(false);
          throw new InterruptedException();
        }
      }
    }
    sendCommand(new QuitGameCommand());
    shutdown();
    return null; 
  }

  @Override
  public void shutdown() {
    state.set(false);
    for (Player it : team) {
      it.close();
    }
    for (Service<?> it : services) {
      it.shutdown();
    }
    for (Future<?> it : future) {
      it.cancel(true);
    }
  }

  /**
   * @return - the player having the specified {@code id} or {@code null} if no such player
   *     was found.
   */
  private Player getPlayer(int id) {
    for (Player player : team) {
      if (player.getId() == id) {
        return player;
      }
    }
    return null;
  }

  /** Initialize clients. */
  private void initClients() {
    List<Couple<Integer,String>> idToName = new ArrayList<>(team.size());
    for (Player player : team) {
      idToName.add(new Couple<Integer,String>(player.getId(),player.getName()));
    }
    sendCommand(new SetPlayerNamesCommand(idToName));
    sendCommand(new StartGameCommand());
  }

  private void processInput() {
    Iterator<Player> it = team.iterator();
    while (it.hasNext()) {
      Player player = it.next();
      if (player.isOnline()) {
        List<Command> commands = player.pull();
        if (commands != null) {
          for (Command command : commands) {
            command.setExecutor(this);
            command.execute();
          }
        }
      } else {
        player.close();
        it.remove();
      }
    }
  }

  public void movePlayerLeft(int id) {
    Iterator<LogicEntity> it = world.getIterator(EntityEnum.PLAYER);
    while (it.hasNext()) {
      LogicEntity player = it.next();
      // TODO separate player from getEntity()
      if (player.getEntity().getId() == id) {
        movePlayer(player,player.getX() - config.speed().player().getDistance());
      }
    }
  }

  public void movePlayerRight(int id) {
    LOGGER.info("Moving id " + id);
    Iterator<LogicEntity> it = world.getIterator(EntityEnum.PLAYER);
    while (it.hasNext()) {
      LogicEntity player = it.next();
      // TODO same
      if (player.getEntity().getId() == id) {
        movePlayer(player,player.getX() + config.speed().player().getDistance());
      }
    }
  }

  public void playerShoot(int id) {
    Iterator<LogicEntity> it = world.getIterator(EntityEnum.PLAYER);
    while (it.hasNext()) {
      LogicEntity player = it.next();
      if (player.getEntity().getId() == id) {
        final int playerW = config.player().getWidth();
        final int bulletW = config.playerBullet().getWidth();
        final int bulletX = player.getX() + playerW / 2 - bulletW / 2;
        final int bulletY = player.getY() - GUARD_PIXELS;
        world.spawnPlayerBullet(player.getEntity().getId(),bulletX,bulletY);
      }
    }
  }

  private void movePlayer(LogicEntity player, int newX) {
    final int playerW = config.player().getWidth();
    final int frameW = config.frame().getWidth();
    if (newX >= GUARD_PIXELS && newX <= frameW - playerW - GUARD_PIXELS) {
      player.move(newX,player.getY());
    }
  }

  private void update() {
    moveEntities();
    invadersShoot();
    collisionDetection();
    if (world.count(EntityEnum.PLAYER) == 0 || world.count(EntityEnum.INVADER) == 0) {
      state.set(false);
    }
  }

  private void moveEntities() {
    Iterator<LogicEntity> it;
    if (invadersMovement.isOn()) {
      it = world.getIterator(EntityEnum.INVADER);
      boolean moveDown = false;
      if (invadersMoveX > 0) {
        int maxW = Integer.MIN_VALUE;
        it = world.getIterator(EntityEnum.INVADER);
        while (it.hasNext()) {
          LogicEntity invader = it.next();
          maxW = Math.max(invader.getX(),maxW);
        }
        if (maxW + config.invader().getWidth() >= config.frame().getWidth() - GUARD_PIXELS) {
          moveDown = true;
        }
      } else {
        int minW = Integer.MAX_VALUE;
        it = world.getIterator(EntityEnum.INVADER);
        while (it.hasNext()) {
          LogicEntity invader = it.next();
          minW = Math.min(invader.getX(),minW);
        }
        if (minW <= GUARD_PIXELS) {
          moveDown = true;
        }
      }
      if (moveDown) {
        int maxH = Integer.MIN_VALUE;
        it = world.getIterator(EntityEnum.INVADER);
        while (it.hasNext()) {
          LogicEntity invader = it.next();
          invader.move(invader.getX(),invader.getY() + invadersMoveY);
          maxH = Math.max(invader.getY(),maxH);
        }
        if (maxH >= config.frame().getHeight() - config.player().getHeight()) {
          sendCommand(new GameOverCommand());
          return;
        }
        invadersMoveX = -invadersMoveX;
      } else {
        it = world.getIterator(EntityEnum.INVADER);
        while (it.hasNext()) {
          LogicEntity invader = it.next();
          invader.move(invader.getX() + invadersMoveX,invader.getY());
        }
      }
      invadersMovement.toggle();
    }
    if (bulletsMovement.isOn()) {
      it = world.getIterator(EntityEnum.INVADER_BULLET);
      while (it.hasNext()) {
        LogicEntity bullet = it.next();
        bullet.move(bullet.getX(),bullet.getY() + config.speed().bullet().getDistance());
        if (bullet.getY() >= config.frame().getHeight() - GUARD_PIXELS) {
          it.remove();
        }
      }
      it = world.getIterator(EntityEnum.PLAYER_BULLET);
      while (it.hasNext()) {
        LogicEntity bullet = it.next();
        bullet.move(bullet.getX(),bullet.getY() - config.speed().bullet().getDistance());
        if (bullet.getY() <= GUARD_PIXELS) {
          it.remove();
        }
      }
      bulletsMovement.toggle();
    }
  }

  private void invadersShoot() {
    if (invadersShooting.isOn()) {
      Iterator<LogicEntity> it = world.getIterator(EntityEnum.INVADER);
      List<LogicEntity> shooters = new ArrayList<LogicEntity>(config.getInvaderCols());
      int maxH = Integer.MIN_VALUE;
      while (it.hasNext()) {
        LogicEntity invader = it.next();
        LogicEntity shooter;
        boolean addToEnd = true;
        for (int index = 0; index < shooters.size(); ++index) {
          shooter = shooters.get(index);
          if (shooter.getX() == invader.getX()) {
            if (invader.getY() > shooter.getY()) {
              shooters.set(index,invader);
            }
            addToEnd = false;
            break;
          }
        }
        if (addToEnd) {
          shooters.add(invader);
        }
      }
      LOGGER.info("SHOOTERS: " + shooters.size());
      int shooter = rng.nextInt(shooters.size());
      LogicEntity invader = shooters.get(shooter);
      int bulletX = invader.getX() + config.invader().getWidth() / 2
        - config.invaderBullet().getWidth() / 2;
      int bulletY = invader.getY() + config.invader().getHeight() + 5;
      world.spawnInvaderBullet(bulletX,bulletY);
      invadersShooting.toggle();
    }
  }

  private void collisionDetection() {
    // TODO optimize
    Iterator<LogicEntity> it = world.getIterator(EntityEnum.INVADER);
    while (it.hasNext()) {
      LogicEntity invader = it.next();
      Iterator<LogicEntity> shieldIt = world.getIterator(EntityEnum.SHIELD);
      LogicEntity shield;
      while (shieldIt.hasNext()) {
        shield = shieldIt.next();
        if (shield.collides(invader)) {
          shieldIt.remove();
        }
      }
    }

    it = world.getIterator(EntityEnum.PLAYER_BULLET);
    while (it.hasNext()) {
      boolean playerBulletGone = false;
      LogicEntity playerBullet = it.next();
      Iterator<LogicEntity> invaderBulletIt = world.getIterator(EntityEnum.INVADER_BULLET);
      LogicEntity invaderBullet;
      while (invaderBulletIt.hasNext()) {
        invaderBullet = invaderBulletIt.next();
        if (playerBullet.collides(invaderBullet)) {
          invaderBulletIt.remove();
          it.remove();
          playerBulletGone = true;
          break;
        }
      }
      if (playerBulletGone) {
        continue;
      }
      Iterator<LogicEntity> invaderIt = world.getIterator(EntityEnum.INVADER);
      LogicEntity invader;
      while (invaderIt.hasNext()) {
        invader = invaderIt.next();
        if (playerBullet.collides(invader)) {
          invaderIt.remove();
          it.remove();
          int shooterId = ((PlayerBullet) playerBullet).getShooterId();
          Player shooter = getPlayer(shooterId);
          if (shooter != null) {
            shooter.push(new IncrementScoreCommand());
          }
          playerBulletGone = true;
          break;
        }
      }
    }

    it = world.getIterator(EntityEnum.PLAYER);
    while (it.hasNext()) {
      LogicEntity player = it.next();
      Iterator<LogicEntity> invaderBulletIt = world.getIterator(EntityEnum.INVADER_BULLET);
      LogicEntity invaderBullet;
      while (invaderBulletIt.hasNext()) {
        invaderBullet = invaderBulletIt.next();
        if (player.collides(invaderBullet)) {
          invaderBulletIt.remove();
          it.remove();
          Player realPlayer = getPlayer(player.getEntity().getId());
          if (realPlayer != null) {
            realPlayer.push(new GameOverCommand());
          }
          break;
        }
      }
    }

    it = world.getIterator(EntityEnum.INVADER_BULLET);
    while (it.hasNext()) {
      LogicEntity invaderBullet = it.next();
      Iterator<LogicEntity> shieldIt = world.getIterator(EntityEnum.SHIELD);
      LogicEntity shield;
      while (shieldIt.hasNext()) {
        shield = shieldIt.next();
        if (invaderBullet.collides(shield)) {
          it.remove();
          shieldIt.remove();
          break;
        }
      }
    }
  }

  /**
   * Put {@code command} into the buffer.
   *
   * @throws NullPointerException - if {@code command} is {@code null}.
   *
   * <p>The buffer will be flushed once {@link flushCommads} is called.
   */
  private void sendCommand(Command command) {
    if (command == null) {
      throw new NullPointerException();
    }
    for (Player it : team) {
      it.push(command);
    }
  }
}
