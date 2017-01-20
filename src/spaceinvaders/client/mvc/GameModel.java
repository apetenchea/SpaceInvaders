package spaceinvaders.client.mvc;

import static java.util.logging.Level.SEVERE;
import static spaceinvaders.exceptions.AssertionsEnum.NULL_ARGUMENT;
import static spaceinvaders.exceptions.AssertionsEnum.BOUNDED_TRANSFER_QUEUE;

import com.google.gson.JsonSyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.TransferQueue;
import java.util.logging.Logger;
import spaceinvaders.client.ClientConfig;
import spaceinvaders.client.network.NetworkConnection;
import spaceinvaders.command.Command;
import spaceinvaders.command.CommandDirector;
import spaceinvaders.command.CommandBuilder;
import spaceinvaders.command.client.ClientCommandBuilder;
import spaceinvaders.exceptions.InterruptedServiceException;
import spaceinvaders.exceptions.SocketOpeningException;
import spaceinvaders.exceptions.CommandNotFoundException;
import spaceinvaders.utility.ServiceState;
import spaceinvaders.utility.Service;

/**
 * Provides the game data.
 *
 * <p>It acts like a gateway to the game's logic and physics, which all happen on the server side.
 */
public class GameModel implements Model {
  private static final Logger LOGGER = Logger.getLogger(GameModel.class.getName());

  private final ClientConfig config = ClientConfig.getInstance();
  private final TransferQueue<String> incomingQueue = new LinkedTransferQueue<>();
  private final CommandDispatcher dispatcher = new CommandDispatcher();
  private final ExecutorService connectionExecutor;
  private final ExecutorService dispatcherExecutor;
  private final ServiceState connectionState = new ServiceState();
  private final ServiceState gameState = new ServiceState();
  private NetworkConnection connection;

  /** Constructs a new game model, initially without any controller. */
  public GameModel() {
    connectionExecutor = Executors.newSingleThreadExecutor();
    dispatcherExecutor = Executors.newSingleThreadExecutor();
  }

  /**
   * Initialize a new game.
   *
   * @throws SocketOpeningException - if the connection could not be established.
   * @throws ExecutionException - if an exception occurs during execution.
   * @throws InterruptedServiceException - if the service is interrupted prior to shutdown.
   * @throws IllegalPortNumberException - if the port parameter is not a valid port value.
   * @throws RejectedExecutionException - if a task cannot be scheduled for execution.
   */
  @Override
  public Void call() throws SocketOpeningException, ExecutionException,
         InterruptedServiceException {
    incomingQueue.clear();
    try {
      connection = new NetworkConnection(incomingQueue);
    } catch (NullPointerException nullPtrException) {
      throw new AssertionError(NULL_ARGUMENT.toString(),nullPtrException);
    }
    List<Future<?>> future = new ArrayList<>();
    try {
      future.add(connectionExecutor.submit(connection));
      future.add(dispatcherExecutor.submit(dispatcher));
    } catch (NullPointerException nullPtrException) {
      throw new AssertionError(NULL_ARGUMENT.toString(),nullPtrException);
    }
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
          throw new InterruptedServiceException(exception);
        }
      }
    }
    return null;
  }

  /**
   * Couple <code>contoller</code> with this model.
   *
   * <p>The controller will receive updates.
   *
   * @throws NullPointerException - if the controller is <code>null</code>.
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
  public void playGame() {
    LOGGER.info("Game is starting.");

    gameState.set(true);
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

  /**
   * Stop service execution.
   *
   * @throws SecurityException - from {@link ExecutorService#shutdown()}.
   * @throws RuntimePermission - from {@link ExecutorService#shutdown()}.
   */
  @Override
  public void shutdown() {
    exitGame();
    dispatcher.shutdown();
    connectionExecutor.shutdownNow();
    dispatcherExecutor.shutdownNow();
  }

  /**
   * Takes data out of the incoming queue and forwards it to the controller.
   *
   * <p>Data is converted into {@link Command}. If <code>null</code> is found in the queue,
   * it is associated with EOF. The controller gets notified with <code>null</code> as well,
   * and any data left in the queue is discarded.
   */
  private class CommandDispatcher extends Observable implements Service<Void> {
    private final ServiceState state = new ServiceState();

    /** 
     * Start converting and forwarding data.
     *
     * @throws InterruptedServiceException - if the service is interrupted prior to shutdown.
     */
    @Override
    public Void call() throws InterruptedServiceException {
      CommandDirector director = new CommandDirector(new ClientCommandBuilder());
      state.set(true);
      while (state.get()) {
        String data = null;
        try {
          data = incomingQueue.take();
        } catch (InterruptedException interruptedException) {
          if (state.get()) {
            state.set(false);
            throw new InterruptedServiceException(interruptedException);
          }
        }
        LOGGER.info(data);
        try {
          director.makeCommand(data);
          setChanged();
          notifyObservers(director.getCommand());
        } catch (JsonSyntaxException jsonException) {
          LOGGER.log(SEVERE,jsonException.toString(),jsonException);
        } catch (CommandNotFoundException commandException) {
          LOGGER.log(SEVERE,commandException.toString(),commandException);
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
