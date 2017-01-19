package spaceinvaders.server.game;

import static java.util.logging.Level.SEVERE;
import static spaceinvaders.command.ProtocolEnum.UDP;

import java.util.List;
import java.util.ArrayList;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Logger;
import spaceinvaders.command.Command;
import spaceinvaders.command.client.SetPlayerNamesCommand;
import spaceinvaders.command.client.StartGameCommand;
import spaceinvaders.game.GameConfig;
import spaceinvaders.command.client.QuitGameCommand;
import spaceinvaders.command.client.PackCommand;
import spaceinvaders.server.game.world.WorldDirector;
import spaceinvaders.server.game.world.ClassicWorldBuilder;
import spaceinvaders.server.game.world.World;
import spaceinvaders.server.player.Player;
import spaceinvaders.game.Entity;
import spaceinvaders.game.EntityEnum;
import spaceinvaders.utility.Couple;
import spaceinvaders.utility.Service;
import spaceinvaders.utility.ServiceState;
import spaceinvaders.utility.AutoSwitch;

/** Game logic and physics happen here. */
public class Game implements Service<Void> {
  private static final Logger LOGGER = Logger.getLogger(Game.class.getName());
  private static final int SLEEP_BETWEEN_FRAMES_MS = 100;

  private final GameConfig config = GameConfig.getInstance();
  private final List<Player> team;
  private final World world;
  private final AutoSwitch invadersMovement = new AutoSwitch(config.speed().invader().getRate()); 
  private final AutoSwitch bulletsMovement = new AutoSwitch(config.speed().bullet().getRate()); 
  private final List<Service<?>> services = new ArrayList<>();
  private final List<Future<?>> future = new ArrayList<>();
  private final ReadWriteLock teamListLock = new ReentrantReadWriteLock();
  private final List<Command> udpBuffer = new ArrayList<>();
  private final List<Command> tcpBuffer = new ArrayList<>();
  private final ServiceState state = new ServiceState();
  private final ExecutorService threadPool;

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
    if (team == null || threadPool == null) {
      throw new NullPointerException();
    }
    this.team = team;
    this.threadPool = threadPool;
    WorldDirector director = new WorldDirector(new ClassicWorldBuilder());
    director.makeWorld(team.size());
    world = director.getWorld();
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
  public Void call() throws InterruptedException {
    LOGGER.info("Game started.");
    initClients();
    future.add(threadPool.submit(invadersMovement));
    future.add(threadPool.submit(bulletsMovement));
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
        send();
        flushCommands();
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

  /** Initialize clients. */
  private void initClients() {
    teamListLock.readLock().lock();
    List<Couple<Integer,String>> idToName = new ArrayList<>(team.size());
    for (Player player : team) {
      idToName.add(new Couple<Integer,String>(player.getId(),player.getName()));
    }
    teamListLock.readLock().unlock();
    sendCommand(new SetPlayerNamesCommand(idToName));
    sendCommand(new StartGameCommand());
  }

  private void processInput() {
    for (Player player : team) {
      List<Command> commands = player.pull();
      for (Command it : commands) {
        command.setExecutor(this);
        command.execute();
      }
    }
  }

  private void update() {
    if (invadersMovement.isOn()) {

      invadersMovement.toggle();
    }
  }

  /** Flush buffered commands for all players. */
  private void flushCommands() {
    teamListLock.readLock().lock();
    for (Player player : team) {
      player.flush();
    }
    teamListLock.readLock().unlock();
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
