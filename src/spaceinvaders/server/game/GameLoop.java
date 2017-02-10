package spaceinvaders.server.game;

import static spaceinvaders.game.EntityEnum.INVADER;
import static spaceinvaders.game.EntityEnum.INVADER_BULLET;
import static spaceinvaders.game.EntityEnum.PLAYER_BULLET;
import static spaceinvaders.game.EntityEnum.SHIELD;
import static spaceinvaders.game.EntityEnum.PLAYER;

import java.util.Random;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import spaceinvaders.game.GameConfig;
import spaceinvaders.command.Command;
import spaceinvaders.game.EntityEnum;
import spaceinvaders.game.GameConfig;
import spaceinvaders.server.game.world.LogicEntity;
import spaceinvaders.server.game.world.World;
import spaceinvaders.server.player.Player;
import spaceinvaders.utility.AutoSwitch;
import spaceinvaders.utility.Service;

/** Handles user input and advances the game simulation. */
class GameLoop implements Service<Void> {
  private static final int GUARD_PIXELS = 32;

  public final GameConfig config = GameConfig.getInstance();
  private final AutoSwitch invadersMovement = new AutoSwitch(config.speed().invader().getRate()); 
  private final AutoSwitch bulletsMovement = new AutoSwitch(config.speed().bullet().getRate()); 
  private final AutoSwitch invadersShooting = new AutoSwitch(config.getInvadersShootingRate()); 
  private final List<Future<?>> future = new ArrayList<>();
  private final List<Command> commandBuf = new ArrayList<>();
  private Integer InvadersVelocityX = config.speed().invader().getDistance();
  private Integer InvadersVelocityY = config.speed().invader().getDistance();
  public final List<Player> team;
  public final World world;
  public final Random rng;
  public final ExecutorService threadPool;

  /**
   * @param team - human players.
   * @param world - game environment.
   * @param rng - pseudorandom number generator.
   * @param threadPool - used for running auto-switches.
   *
   * @throws NullPointerException - if an argument is {@code null}.
   */
  public GameLoop(List<Player> team, World world, Random rng, ExecutorService threadPool) {
    if (team == null || world == null || rng == null || threadPool == null) {
      throw new NullPointerException();
    }
    this.team = team;
    this.world = world;
    this.rng = rng;
    this.threadPool = threadPool;
  }

  /**
   * Start auto-switches.
   *
   * @throws RejectedExecutionException - if the task cannot be executed.
   */
  @Override
  public Void call() {
    future.add(threadPool.submit(invadersMovement));
    future.add(threadPool.submit(bulletsMovement));
    future.add(threadPool.submit(invadersShooting));
    return null;
  }

  @Override
  public void shutdown() {
    invadersMovement.shutdown();
    bulletsMovement.shutdown();
    invadersShooting.shutdown();
    for (Future<?> it : future) {
      it.cancel(true);
    }
  }

  public Command[] drainCommands() {
    Command[] commands = new Command[commandBuf.size()];
    commandBuf.toArray(commands);
    commandBuf.clear();
    return commands;
  }

  /*
   * Handle user input that has happened since the last call.
   */
  public void processInput() {
    Iterator<Player> it = team.iterator();
    while (it.hasNext()) {
      Player player = it.next();
      if (player.isOnline()) {
        List<Command> commands = player.pull();
        for (Command command : commands) {
          command.setExecutor(this);
          command.execute();
        }
      } else {
        player.close();
        // remove player from screen
        it.remove();
      }
    }
  }

  public void movePlayerLeft(int id) {
    Iterator<LogicEntity> it = world.getIterator(PLAYER);
    while (it.hasNext()) {
      LogicEntity player = it.next();
      // TODO separate player from getEntity()
      if (player.getEntity().getId() == id) {
        movePlayer(player,player.getX() - config.speed().player().getDistance());
      }
    }
  }

  public void movePlayerRight(int id) {
    Iterator<LogicEntity> it = world.getIterator(EntityEnum.PLAYER);
    while (it.hasNext()) {
      LogicEntity player = it.next();
      // TODO same
      if (player.getEntity().getId() == id) {
        movePlayer(player,player.getX() + config.speed().player().getDistance());
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

  /**
   * Advance the game simulation one step.
   */
  public void update() {
    entitiesMove();
    entitiesShoot();
    detectCollision();
  }

  /** Move all entities controlled by the CPU. */
  private void entitiesMove() {
    Iterator<LogicEntity> it;

    /* Move invaders */
    if (invadersMovement.isOn()) {
      boolean moveDown = false;
      if (InvadersVelocityX > 0) {
        int maxW = Integer.MIN_VALUE;
        it = world.getIterator(INVADER);
        while (it.hasNext()) {
          LogicEntity invader = it.next();
          maxW = Math.max(maxW,invader.getX());
        }
        if (maxW + config.invader().getWidth() >= config.invader().getWidth() + GUARD_PIXELS) {
          moveDown = true;
        }
      } else {
        int minW = Integer.MAX_VALUE;
        it = world.getIterator(INVADER);
        while (it.hasNext()) {
          LogicEntity invader = it.next();
          minW = Math.min(minW,invader.getX());
        }
        if (minW <= GUARD_PIXELS) {
          moveDown = true;
        }
      }
      if (moveDown) {
        // Change horizontal direction.
        InvadersVelocityX = -InvadersVelocityX;
        int maxH = Integer.MIN_VALUE;
        it = world.getIterator(INVADER);
        while (it.hasNext()) {
          LogicEntity invader = it.next();
          invader.move(invader.getX(),invader.getY() + InvadersVelocityY);
          maxH = Math.max(maxH,invader.getY());
        }
        if (maxH >= config.frame().getHeight() - config.player().getHeight()) {
          /*
          sendCommand(new MoveInvadersCommand(0,InvadersVelocityY));
          TODO kill players here
          sendCommand(new GameOverCommand());
          */
          return;
        }
      } else {
        it = world.getIterator(INVADER);
        while (it.hasNext()) {
          LogicEntity invader = it.next();
          invader.move(invader.getX() + InvadersVelocityX,invader.getY());
          /*
          sendCommand(new MoveEntitiesCommand(INVADER,InvadersVelocityX,0));
          */
        }
      }
      invadersMovement.toggle();
    }

    /* Move bullets. */
    if (bulletsMovement.isOn()) {
      int distance = config.speed().bullet().getDistance();

      /* Invader bullets */
      it = world.getIterator(INVADER_BULLET);
      while (it.hasNext()) {
        LogicEntity bullet = it.next();
        bullet.move(bullet.getX(),bullet.getY() + distance);
        if (bullet.getY() >= config.frame().getHeight() - GUARD_PIXELS) {
          it.remove();
        }
      }
      //sendCommand(new MoveEntitiesCommand(INVADER_BULLET,0,distance);

      /* Player bullets */
      it = world.getIterator(PLAYER_BULLET);
      while (it.hasNext()) {
        LogicEntity bullet = it.next();
        bullet.move(bullet.getX(),bullet.getY() - distance);
        if (bullet.getY() <= GUARD_PIXELS) {
          it.remove();
        }
      }
      //sendCommand(new MoveEntitiesCommand(PLAYER_BULLET,0,distance);
      
      bulletsMovement.toggle();
    }
  }

  private void entitiesShoot() {
    Iterator<LogicEntity> it;

    /* Invaders shoot. */
    if (invadersShooting.isOn()) {
      /* Get the lowest invader in each column. */
      List<LogicEntity> shooters = new ArrayList<LogicEntity>(config.getInvaderCols());
      int maxH = Integer.MIN_VALUE;
      it = world.getIterator(INVADER);
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

      LogicEntity shooter = shooters.get(rng.nextInt(shooters.size()));
      int bulletX = shooter.getX() + config.invader().getWidth() / 2
        - config.invaderBullet().getWidth() / 2;
      int bulletY = shooter.getY() + config.invader().getHeight() + 5;
      world.spawnInvaderBullet(bulletX,bulletY);
      // TODO need to get the bullet id
      //sendCommand(new SpawnEntityCommand(PLAYER_BULLET,bulletX,bulletY);
      invadersShooting.toggle();
    }
  }

  private void detectCollision() {
    Iterator<LogicEntity> invaderIt, playerIt, shieldIt, playerBulletIt, invaderBulletIt;

    /* Invaders vs shields. */
    invaderIt = world.getIterator(INVADER);
    while (invaderIt.hasNext()) {
      LogicEntity invader = invaderIt.next();
      shieldIt = world.getIterator(SHIELD);
      LogicEntity shield;
      while (shieldIt.hasNext()) {
        shield = shieldIt.next();
        if (shield.collides(invader)) {
          // TODO get the id
          //sendCommand(new DestroyEntityCommand(SHIELD,
          shieldIt.remove();
        }
      }
    }

    /* Player bullets vs invader bullets. */
    playerBulletIt = world.getIterator(PLAYER_BULLET);
    while (playerBulletIt.hasNext()) {
      LogicEntity playerBullet = playerBulletIt.next();
      invaderBulletIt = world.getIterator(INVADER_BULLET);
      LogicEntity invaderBullet;
      while (invaderBulletIt.hasNext()) {
        invaderBullet = invaderBulletIt.next();
        if (playerBullet.collides(invaderBullet)) {
          // commands
          invaderBulletIt.remove();
          playerBulletIt.remove();
          break;
        }
      }
    }

    /* Player bullets vs invaders. */
    playerBulletIt = world.getIterator(PLAYER_BULLET);
    while (playerBulletIt.hasNext()) {
      boolean playerBulletDestroyed = false;
      LogicEntity playerBullet = playerBulletIt.next();
      invaderIt = world.getIterator(INVADER);
      while (invaderIt.hasNext()) {
        LogicEntity invader = invaderIt.next();
        if (playerBullet.collides(invader)) {
          // commands
          // TODO update score
          invaderIt.remove();
          playerBulletIt.remove();
          break;
        }
      }
    }

    /* Invader bullets vs shields. */
    invaderBulletIt = world.getIterator(INVADER_BULLET);
    while (invaderBulletIt.hasNext()) {
      LogicEntity invaderBullet = invaderBulletIt.next();
      shieldIt = world.getIterator(SHIELD);
      LogicEntity shield;
      while (shieldIt.hasNext()) {
        shield = shieldIt.next();
        if (invaderBullet.collides(shield)) {
          // commands
          invaderBulletIt.remove();
          shieldIt.remove();
          break;
        }
      }
    }

    /* Invader bullets vs players. */
    invaderBulletIt = world.getIterator(INVADER_BULLET);
    while (invaderBulletIt.hasNext()) {
      LogicEntity invaderBullet = invaderBulletIt.next();
      playerIt = world.getIterator(PLAYER);
      LogicEntity player;
      while (playerIt.hasNext()) {
        player = playerIt.next();
        if (invaderBullet.collides(player)) {
          // commands
          invaderBulletIt.remove();
          // also remove the real player
          playerIt.remove();
          break;
        }
      }
    }
  }
}
