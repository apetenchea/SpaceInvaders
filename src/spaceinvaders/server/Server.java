package spaceinvaders.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import spaceinvaders.exceptions.SocketOpeningException;
import spaceinvaders.server.game.GameManager;
import spaceinvaders.server.network.ConnectionManager;
import spaceinvaders.server.players.PlayerManager;

/**
 * Server-side of the game.
 *
 * <p>Inside the server happens all the game logic.
 * A client sends data and the server may send back a response, in case of a genuine request.
 * The game cannot be played without a running server.
 */
public class Server implements Callable<Void> {
  private static final Logger LOGGER = Logger.getLogger(Server.class.getName());
  private static final String QUIT_COMMAND = "quit";

  private ConnectionManager connectionManager;
  private PlayerManager playerManager;
  private GameManager gameManager;

  private ExecutorService connectionManagerExecutor;
  private ExecutorService playerManagerExecutor;
  private ExecutorService inputReaderExecutor;

  /**
   * Construct a server that will listen on the specified port.
   */
  public Server(int port) {
    try {
      connectionManager = new ConnectionManager(port);
    } catch (SocketOpeningException exception) {
      LOGGER.log(Level.SEVERE,exception.getMessage(),exception);
      shutdown();
    }
    gameManager = new GameManager();
    playerManager = new PlayerManager(connectionManager.getConnectionQueue(),
        connectionManager.getPacketQueue());
    playerManager.addObserver(gameManager);

    connectionManagerExecutor = Executors.newSingleThreadExecutor();
    playerManagerExecutor = Executors.newSingleThreadExecutor();
    inputReaderExecutor = Executors.newSingleThreadExecutor();
  }

  @Override
  public Void call() {
    Future<Void> connectionManagerFuture = connectionManagerExecutor.submit(connectionManager);
    Future<Void> playerManagerFuture = playerManagerExecutor.submit(playerManager);
    inputReaderExecutor.submit(new Callable<Void>() {
      @Override
      public Void call() {
        BufferedReader inputReader = new BufferedReader(new InputStreamReader(System.in));
        String input = null;
        while (true) {
          try {
            input = inputReader.readLine();
          } catch (IOException exception) {
            LOGGER.log(Level.SEVERE,exception.getMessage(),exception);
            break;
          }
          if (input.equals(QUIT_COMMAND)) {
            break;
          }
        }
        shutdown();
        return null;
      }
    });

    LOGGER.info("Enter quit to exit.");
    try {
      connectionManagerFuture.get();
      playerManagerFuture.get();
    } catch (InterruptedException exception) {
      LOGGER.log(Level.SEVERE,exception.toString(),exception);
    } catch (Exception exception) {
      LOGGER.log(Level.SEVERE,exception.getMessage(),exception);
    } finally {
      shutdown();
      connectionManagerExecutor.shutdownNow();
      playerManagerExecutor.shutdownNow();
      inputReaderExecutor.shutdownNow();
    }

    return null;
  }

  private void shutdown() {
    if (connectionManager != null) {
      connectionManager.shutdown();
    }
    if (playerManager != null) {
      playerManager.shutdown();
    }
    if (gameManager != null) {
      gameManager.shutdown();
    }
  }
}
