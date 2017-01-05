package spaceinvaders.server.players;

import java.io.IOException;
import java.net.Socket;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import spaceinvaders.exceptions.ClosingSocketException;
import spaceinvaders.exceptions.InterruptedServiceException;
import spaceinvaders.exceptions.SocketInputStreamException;
import spaceinvaders.exceptions.SocketOutputStreamException;
import spaceinvaders.utility.ServiceState;

/**
 * Manages all players.
 */
public class PlayerManager extends Observable implements Callable<Void>, Observer {
  private static final Logger LOGGER = Logger.getLogger(PlayerManager.class.getName());
  private static final int MAX_PLAYERS = 15;

  private PlayerManager thisInstance;

  private BlockingQueue<Socket> connectionQueue;
  private BlockingQueue<String> packetQueue;
  private ConcurrentMap<Integer, Player> playersMap;

  private ExecutorService connectionHandler;
  private ExecutorService packetHandler;
  private ExecutorService cachedThreadPool;
  private ServiceState state;

  /**
   * Construct a manager that will two queues for taking data in.
   */
  public PlayerManager(BlockingQueue<Socket> connectionQueue, BlockingQueue<String> packetQueue) {
    thisInstance = this;
    this.connectionQueue = connectionQueue;
    this.packetQueue = packetQueue;
    playersMap = new ConcurrentHashMap<>();

    connectionHandler = Executors.newSingleThreadExecutor();
    packetHandler = Executors.newSingleThreadExecutor();
    cachedThreadPool = Executors.newCachedThreadPool();

    state = new ServiceState(true);
  }

  @Override
  public Void call() throws Exception {
    Future<Void> connectionHandlerFuture = connectionHandler.submit(new Callable<Void>() {
      @Override
      public Void call() throws InterruptedServiceException {
        while (state.get()) {
          Socket clientSocket = null;
          try {
            clientSocket = connectionQueue.take();
          } catch (InterruptedException exception) {
            if (state.get()) {
              throw new InterruptedServiceException(exception);
            }
          }
          if (clientSocket == null) {
            continue;
          }
          if (playersMap.size() < MAX_PLAYERS) {
            try {
              Player client = new Player(clientSocket,cachedThreadPool);
              cachedThreadPool.submit(client);
              client.addObserver(thisInstance);
              playersMap.put(client.hashCode(),client);
              setChanged();
              notifyObservers(client);
            } catch (SocketInputStreamException | SocketOutputStreamException exception) {
              LOGGER.log(Level.SEVERE,exception.getMessage(),exception);
            } 
          } else {
            try {
              clientSocket.close();
            } catch (IOException exception) {
              LOGGER.log(Level.SEVERE,exception.getMessage(),exception);
            }
          }
        }
        return null;
      }
    });

    Future<Void> packetHandlerFuture = packetHandler.submit(new Callable<Void>() {
      @Override
      public Void call() throws InterruptedServiceException {
        while (state.get()) {
          try {
            String data = packetQueue.take();
          } catch (InterruptedException exception) {
            if (state.get()) {
              throw new InterruptedServiceException(exception);
            }
          }
        }
        return null;
      }
    });

    try {
      connectionHandlerFuture.get();
      packetHandlerFuture.get();
    } catch (ExecutionException exception) {
      throw new Exception(exception.getCause());
    } catch (InterruptedException exception) {
      LOGGER.log(Level.SEVERE,exception.getMessage(),exception);
    } finally {
      state.set(false);
    }

    return null; 
  }

  @Override
  public void update(Observable observable, Object obj) {
    if (observable instanceof Player) {
      Player leavingPlayer = (Player) observable;
      playersMap.remove(leavingPlayer.hashCode());
    }
  }
  /**
   * Shutdown service.
   *
   * <p>All other threads started by this service are stopped.
   */
  public void shutdown() {
    state.set(false);
    connectionHandler.shutdownNow();
    packetHandler.shutdownNow();
    for (ConcurrentMap.Entry<Integer, Player> entry : playersMap.entrySet()) {
      try {
        entry.getValue().close();
      } catch (ClosingSocketException exception) {
        LOGGER.log(Level.SEVERE,exception.getMessage(),exception);
      }
    }
    cachedThreadPool.shutdownNow();
  }
}
