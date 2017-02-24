package spaceinvaders.server;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import spaceinvaders.exceptions.IllegalPortNumberException;
import spaceinvaders.exceptions.SocketOpeningException;
import spaceinvaders.server.game.GameManager;
import spaceinvaders.server.network.ConnectionManager;
import spaceinvaders.server.player.PlayerManager;
import spaceinvaders.utility.Service;
import spaceinvaders.utility.ServiceState;

/**
 * Server-side of the game.
 *
 * <p>Inside here happens all the game logic. It cannot be played without a running server.
 * 
 * <p>A player uses the client to communicate with the server. Once the connection is established,
 * the server starts the game. The server sends game events to the client (for exemple the
 * invaders have moved). Meanwhile the client sends the actions of the user. Once the game is over,
 * the client is disconnected.
 */
public class Server implements Service<Void> {
  private final PlayerManager playerManager = new PlayerManager();
  private final GameManager gameManager = new GameManager();
  private final ConnectionManager connectionManager;
  private final ExecutorService connectionManagerExecutor;
  private final ExecutorService playerManagerExecutor;
  private final ExecutorService gameManagerExecutor;
  private final ServiceState state = new ServiceState();

  /**
   * Construct a server that will listen for connections on port {@code port}.
   * 
   * @throws SocketOpeningException if a socket cannot be opened on the specified port.
   * @throws SecurityException if a security manager does not allow an operation.
   * @throws IllegalPortNumberException if the specified port number is invalid.
   */
  public Server(int port) throws SocketOpeningException {
    connectionManager = new ConnectionManager(port);
    connectionManager.addPlayerManager(playerManager);
    playerManager.addObserver(gameManager);
    connectionManagerExecutor = Executors.newSingleThreadExecutor();
    playerManagerExecutor = Executors.newSingleThreadExecutor();
    gameManagerExecutor = Executors.newSingleThreadExecutor();
    state.set(true);
  }

  /**
   * Start listening for connections and handling players.
   *
   * @throws ExecutionException if an exception occurs during execution.
   * @throws InterruptedException if the service is interrupted prior to shutdown.
   * @throws RejectedExecutionException if a subtask cannot be executed.
   */
  @Override
  public Void call() throws ExecutionException, InterruptedException {
    List<Future<?>> future = new ArrayList<>();
    future.add(connectionManagerExecutor.submit(connectionManager));
    future.add(playerManagerExecutor.submit(playerManager));
    future.add(gameManagerExecutor.submit(gameManager));
    final long checkingRateMs = 1000;
    while (state.get()) {
      try {
        for (Future<?> it : future) {
          if (it.isDone()) {
            state.set(false);
            it.get();
          }
        }
        Thread.sleep(checkingRateMs);
      } catch (CancellationException | InterruptedException exception) {
        if (state.get()) {
          state.set(false);
          throw new InterruptedException();
        }
      }
    }
    state.set(false);
    return null;
  }

  @Override
  public void shutdown() {
    state.set(false);
    connectionManager.shutdown();
    playerManager.shutdown();
    gameManager.shutdown();
    connectionManagerExecutor.shutdownNow();
    playerManagerExecutor.shutdownNow();
    gameManagerExecutor.shutdownNow();
  }

  /**
   * @return true if the server is running, false otherwise.
   */
  public boolean isRunning() {
    return state.get();
  }
}
