package spaceinvaders.client.mvc;

import static java.util.logging.Level.SEVERE;

import com.google.gson.JsonSyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.Future;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.TransferQueue;
import java.util.logging.Logger;
import spaceinvaders.client.network.NetworkConnection;
import spaceinvaders.command.Command;
import spaceinvaders.command.CommandDirector;
import spaceinvaders.command.client.ClientCommandBuilder;
import spaceinvaders.exceptions.SocketOpeningException;
import spaceinvaders.exceptions.CommandNotFoundException;
import spaceinvaders.utility.ServiceState;
import spaceinvaders.utility.Service;

/**
 * Provides the game data.
 *
 * <p>Gateway to the server.
 */
public class GameModel implements Model {
  private static final Logger LOGGER = Logger.getLogger(GameModel.class.getName());

  private final TransferQueue<String> incomingQueue = new LinkedTransferQueue<>();
  private final CommandDispatcher dispatcher = new CommandDispatcher();
  private final ExecutorService connectionExecutor = Executors.newSingleThreadExecutor();
  private final ExecutorService dispatcherExecutor = Executors.newSingleThreadExecutor();
  private final List<Future<?>> future = new ArrayList<>();
  private final ServiceState connectionState = new ServiceState();
  private final ServiceState gameState = new ServiceState();
  private NetworkConnection connection;

  public GameModel() {
    future.add(dispatcherExecutor.submit(dispatcher));
  }

  /**
   * Initialize a new game.
   *
   * @throws SocketOpeningException - if the connection could not be established.
   * @throws ExecutionException - if an exception occurs during execution.
   * @throws InterruptedException - if the service is interrupted prior to shutdown.
   * @throws IllegalPortNumberException - if the port parameter is not a valid port value.
   * @throws RejectedExecutionException - if a task cannot be scheduled for execution.
   */
  @Override
  public Void call() throws SocketOpeningException, ExecutionException,
         InterruptedException {
    incomingQueue.clear();
    // This will open up a network connection, and might throw exceptions.
    connection = new NetworkConnection(incomingQueue);
    future.add(connectionExecutor.submit(connection));
    final long checkingRateMilliseconds = 1000;
    connectionState.set(true);
    while (connectionState.get()) {
      try {
        for (Future<?> it : future) {
          if (it.isDone()) {
            connectionState.set(false);
            gameState.set(false);
            it.get();
          }
        }
        Thread.sleep(checkingRateMilliseconds);
      } catch (CancellationException | InterruptedException exception) {
        if (connectionState.get()) {
          gameState.set(false);
          throw new InterruptedException();
        }
        break;
      }
    }
    return null;
  }

  /**
   * Couple {@code contoller} with this model.
   *
   * <p>The controller will receive updates.
   *
   * @throws NullPointerException - if an argument is {@code null}.
   */
  @Override
  public void addController(Controller controller) {
    if (controller == null) {
      throw new NullPointerException();
    }
    dispatcher.addObserver(controller);
  }

  /**
   * Send a command to the server.
   *
   * @throws NullPointerException - if there is no connection.
   */
  @Override
  public void doCommand(Command command) {
    if (connection == null) {
      throw new NullPointerException();
    }
    connection.send(command);
  }

  @Override
  public void exitGame() {
    LOGGER.info("Game is over.");

    connectionState.set(false);
    gameState.set(false);
    if (connection != null) {
      connection.shutdown();
    }
    connection = null;
  }

  @Override
  public boolean getGameState() {
    return gameState.get();
  }

  @Override
  public void setGameState(boolean state) {
    gameState.set(state);
  }

  @Override
  public void shutdown() {
    exitGame();
    dispatcher.shutdown();
    connectionExecutor.shutdownNow();
    dispatcherExecutor.shutdownNow();
  }

  /** Takes data out of the incoming queue and forwards it to the controller. */
  private class CommandDispatcher extends Observable implements Service<Void> {
    private final ServiceState state = new ServiceState();

    /** 
     * Start converting and forwarding data.
     *
     * @throws InterruptedException - if the service is interrupted prior to shutdown.
     */
    @Override
    public Void call() throws InterruptedException {
      CommandDirector director = new CommandDirector(new ClientCommandBuilder());
      state.set(true);
      while (state.get()) {
        String data = null;
        try {
          data = incomingQueue.take();
        } catch (InterruptedException interruptedException) {
          if (state.get()) {
            state.set(false);
            throw new InterruptedException();
          }
          break;
        }
        if (data.equals("EOF")) {
          // Connection over.
          state.set(false);
          break;
        }
        // TODO
        System.out.println("Got: " + data);
        try {
          director.makeCommand(data);
          setChanged();
          // Notify the controller.
          notifyObservers(director.getCommand());
        } catch (JsonSyntaxException | CommandNotFoundException exception) {
          LOGGER.log(SEVERE,exception.toString(),exception);
        }
      }
      return null;
    }

    @Override
    public void shutdown() {
      state.set(false);
    }
  }
}
