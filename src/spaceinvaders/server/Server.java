package spaceinvaders.server;

import static java.util.logging.Level.SEVERE;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Logger;
import spaceinvaders.exceptions.InterruptedServiceException;
import spaceinvaders.exceptions.SocketOpeningException;
import spaceinvaders.server.game.GameManager;
import spaceinvaders.server.network.ConnectionManager;
import spaceinvaders.server.player.PlayerManager;
import spaceinvaders.utility.Service;
import spaceinvaders.utility.ServiceState;

/**
 * Server-side of the game.
 *
 * <p>Inside the server happens all the game logic.
 * A client sends data and the server may send back a response, in case of a genuine request.
 * The game cannot be played without a running server.
 */
public class Server implements Service<Void> {
  private static final Logger LOGGER = Logger.getLogger(Server.class.getName());

  private final ConnectionManager connectionManager;
  private final PlayerManager playerManager = new PlayerManager();
  private GameManager gameManager = new GameManager();
  private ExecutorService connectionManagerExecutor;
  private ExecutorService playerManagerExecutor;
  private ExecutorService gameManagerExecutor;
  private ServiceState state = new ServiceState();

  /**
   * Construct a server that will listen on port <code>port</code>.
   * 
   * @throws SocketOpeningException - if a socket cannot be opened on the specified port.
   * @throws SecurityException - if a security manager does not allow an operation.
   * @throws IllegalPortNumberException - if the specified port number is invalid.
   */
  public Server(int port) throws SocketOpeningException {
    connectionManager = new ConnectionManager(port);
    connectionManagerExecutor = Executors.newSingleThreadExecutor();
    playerManagerExecutor = Executors.newSingleThreadExecutor();
    gameManagerExecutor = Executors.newSingleThreadExecutor();
    connectionManager.addObserver(playerManager);
    playerManager.addObserver(gameManager);
    state.set(true);
  }

  /**
   * Start the server.
   *
   * @throws ExecutionException - if an exception occurs during execution.
   * @throws InterruptedServiceException - if the service is interrupted prior to shutdown.
   * @throws RejectedExecutionException - if the task cannot be executed.
   */
  @Override
  public Void call() throws ExecutionException, InterruptedServiceException {
    LOGGER.info("Server is starting.");

    List<Future<?>> future = new ArrayList<>();
    future.add(connectionManagerExecutor.submit(connectionManager));
    future.add(playerManagerExecutor.submit(playerManager));
    future.add(gameManagerExecutor.submit(gameManager));
    final long checkingRateMilliseconds = 1000;
    while (state.get()) {
      try {
        for (Future<?> it : future) {
          if (it.isDone()) {
            state.set(false);
            it.get();
          }
        }
        Thread.sleep(checkingRateMilliseconds);
      } catch (CancellationException | InterruptedException exception) {
        if (state.get()) {
          state.set(false);
          throw new InterruptedServiceException(exception);
        }
      }
    }
    return null;
  }

  /**
   * Stop service execution.
   *
   * @throws SecurityException - from {@link ExecutorService#shutdown()}.
   * @throws RuntimePermission - from {@link ExecutorService#shutdown()}.
   */
  @Override
  public void shutdown() {
    LOGGER.info("Server is shutting down.");

    state.set(false);
    connectionManager.shutdown();
    playerManager.shutdown();
    gameManager.shutdown();
    connectionManagerExecutor.shutdownNow();
    playerManagerExecutor.shutdownNow();
    gameManagerExecutor.shutdownNow();
  }

  public boolean isRunning() {
    return state.get();
  }
}
