package spaceinvaders.server.player;

import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.TransferQueue;
import spaceinvaders.command.Command;
import spaceinvaders.command.client.SetPlayerIdCommand;
import spaceinvaders.command.server.ConfigurePlayerCommand;
import spaceinvaders.server.game.GameManager;
import spaceinvaders.server.network.Connection;
import spaceinvaders.utility.Service;
import spaceinvaders.utility.ServiceState;

/**
 * Manages all players.
 *
 * <p>Wraps a {@link spaceinvaders.server.network.Connection} into a 
 * {@link spaceinvaders.server.player.Player} and forwards it to the
 * {@link spaceinvaders.server.game.GameManager}.
 *
 * <p>It takes connections as an observer to {@link spaceinvaders.server.network.SocketWrapper}.
 * {@link spaceinvaders.server.game.Game} is notified when a
 * {@link Player} is ready to join a game.
 */
public class PlayerManager extends Observable implements Observer, Service<Void> {
  private final TransferQueue<Connection> connectionQueue = new LinkedTransferQueue<>();
  private final ServiceState state = new ServiceState();
  private final ExecutorService threadPool = Executors.newCachedThreadPool();

  public PlayerManager() {
    state.set(true);
  }

  /** Receive a new connection. */
  @Override
  public void update(Observable observable, Object arg) {
    if (!(arg instanceof Connection && connectionQueue.offer((Connection) arg))) {
      // This should never happen.
      throw new AssertionError();
    }
  }

  /**
   * Get connections out of the transfer queue and create new players.
   *
   * <p>When a {@link spaceinvaders.server.network.Connection} arrives, it is transformed into a
   * {@link spaceinvaders.server.player.Player}. An ID is sent to the corresponding client. If the
   * client responds with the appropriate command within a second, the player is kept. Otherwise, it
   * is discarded.
   *
   * @throws InterruptedException if the service is interrupted prior to shutdown.
   */
  @Override
  public Void call() throws InterruptedException {
    final int responseTimeoutMilliseconds = 1000;
    while (state.get()) {
      Connection connection = null;
      try {
        connection = connectionQueue.take();
      } catch (InterruptedException intException) {
        if (state.get()) {
          throw new InterruptedException();
        }
        break;
      }
      Player player = new Player(connection,threadPool);
      player.push(new SetPlayerIdCommand(player.getId()));
      player.flush();
      try {
        Thread.sleep(responseTimeoutMilliseconds);
      } catch (InterruptedException intException) {
        if (state.get()) {
          throw new InterruptedException();
        }
        break;
      }
      List<Command> commands = player.pull();
      if (commands.size() == 1) {
        Command command = commands.get(0);
        if (!(command instanceof ConfigurePlayerCommand)) {
          // This should never happen.
          throw new AssertionError();
        }
        command.setExecutor(player);
        command.execute();
        setChanged();
        // Notify the game manager.
        notifyObservers(player);
      } else {
        // Player did not respect the protocol or he went offline.
        player.close();
      }
    }
    return null; 
  }

  @Override
  public void shutdown() {
    state.set(false);
    threadPool.shutdownNow();
  }

  /**
   * Set the {@link spaceinvaders.server.game.GameManager} to which all valid players are going to
   * be forwarded.
   *
   * @throws NullPointerException if the argument is {@code null}.
   */
  public void addGameManager(GameManager gameManager) {
    if (gameManager == null) {
      throw new NullPointerException();
    }
    addObserver(gameManager);
  }
}
