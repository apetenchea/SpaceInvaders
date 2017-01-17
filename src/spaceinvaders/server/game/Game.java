package spaceinvaders.server.game;

import static java.util.logging.Level.SEVERE;
import static spaceinvaders.command.ProtocolEnum.UDP;

import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Logger;
import spaceinvaders.command.Command;
import spaceinvaders.command.client.SetPlayerNamesCommand;
import spaceinvaders.command.client.StartGameCommand;
import spaceinvaders.command.client.QuitGameCommand;
import spaceinvaders.command.client.PackCommand;
import spaceinvaders.server.player.Player;
import spaceinvaders.utility.Couple;
import spaceinvaders.utility.Service;
import spaceinvaders.utility.ServiceState;

/** Game logic and physics happen here. */
public class Game implements Service<Void> {
  private static final Logger LOGGER = Logger.getLogger(Game.class.getName());

  private final List<Player> team;
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
   * @throws NullPointerException - if any of the arguments is {@code null}.
   */
  public Game(List<Player> team, ExecutorService threadPool) {
    if (team == null || threadPool == null) {
      throw new NullPointerException();
    }
    this.team = team;
    this.threadPool = threadPool;
    state.set(true);
  }

  @Override
  public Void call() {
    LOGGER.info("Game started");
    sendCommand(new QuitGameCommand());
    flushCommands();
    return null; 
  }

  @Override
  public void shutdown() {
    state.set(false);
  }

  private void initPlayers() {
    teamListLock.readLock().lock();
    List<Couple<Integer,String>> idToName = new ArrayList<>(team.size());
    for (Player player : team) {
      idToName.add(new Couple<Integer,String>(player.getId(),player.getName()));
    }
    teamListLock.readLock().unlock();
    sendCommand(new SetPlayerNamesCommand(idToName));
    sendCommand(new StartGameCommand());
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
    if (command.getProtocol() == UDP) {
      udpBuffer.add(command);
    } else {
      tcpBuffer.add(command);
    }
  }

  /** Flush buffered commands to all players. */
  private void flushCommands() {
    Command command = new PackCommand(new ArrayList<Command>(udpBuffer));
    udpBuffer.clear();
    teamListLock.readLock().lock();
    for (Player player : team) {
      player.push(command);
    }
    for (Command it : tcpBuffer) {
      for (Player player : team) {
        player.push(it);
      }
    } 
    teamListLock.readLock().unlock();
  }
}
