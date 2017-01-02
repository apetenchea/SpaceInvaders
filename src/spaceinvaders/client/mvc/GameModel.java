package spaceinvaders.client.mvc;

import static java.util.concurrent.TimeUnit.SECONDS;
import static spaceinvaders.client.ErrorsEnum.LOST_CONNECTION;
import static spaceinvaders.client.ErrorsEnum.SERVER_TIMEOUT;
import static spaceinvaders.client.ErrorsEnum.UNEXPECTED_ERROR;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import spaceinvaders.client.ClientConfig;
import spaceinvaders.client.network.ConnectionNotAllowedException;
import spaceinvaders.client.network.InvalidConnectionConfigurationException;
import spaceinvaders.client.network.NetworkConnection;
import spaceinvaders.client.network.ServerNotFoundException;
import spaceinvaders.client.network.SocketIoException;
import spaceinvaders.client.network.TcpConnection;
import spaceinvaders.client.network.UdpConnection;

/**
 * Provides the game data.
 *
 * <p>It acts like a gateway to the game's logic and physics, which all happen at the server side.
 * This model is used to access the game data comming from the server, and sending data to it.
 *
 * @see spaceinvaders.client.mvc.GameController
 * @see spaceinvaders.client.mvc.GameView
 */
public class GameModel extends Observable implements Model {
  private static final long PING_TIME_INTERVAL_MILLISECONDS = 2000;
  private static final long SERVER_TIMEOUT_SECONDS = 10;

  private BlockingQueue<String> receivingQueue;
  private BlockingQueue<String> forwardingQueue;
  private AtomicBoolean gameState;
  private ReadWriteLock checkGameStateLock;
  private ExecutorService outgoingPing;
  private ExecutorService dataReceiver;
  private ExecutorService transferWorker;
  private NetworkConnection tcp;
  private NetworkConnection udp;
  private Integer playerId;

  /**
   * Constructs a new model that is initially inactive.
   */
  public GameModel() {
    forwardingQueue = new LinkedBlockingQueue<>();
    receivingQueue = new LinkedBlockingQueue<>();
    gameState = new AtomicBoolean(false);
    checkGameStateLock = new ReentrantReadWriteLock();
    outgoingPing = Executors.newSingleThreadExecutor();
    dataReceiver = Executors.newSingleThreadExecutor();
    transferWorker = Executors.newSingleThreadExecutor();
  }

  @Override
  public String[] getData() {
    List<String> data = new ArrayList<>();
    forwardingQueue.drainTo(data);
    return data.toArray(new String[0]);
  }

  @Override
  public void addController(Controller controller) {
    addObserver(controller);
  }

  @Override
  public void exitGame() throws SocketIoException {
    setGameState(false);
    try {
      if (tcp != null) {
        tcp.close();
      }
      if (udp != null) {
        udp.close();
      }
    } catch (SocketIoException exception) {
      throw exception;
    }
  }

  @Override
  public void initNewGame(ClientConfig config) throws
      ServerNotFoundException,
      SocketIoException,
      ConnectionNotAllowedException,
      InvalidConnectionConfigurationException {
    playerId = config.getId();
    String address = config.getServerAddr();
    Integer port = config.getServerPort();
    tcp = new TcpConnection(address,port);
    udp = new UdpConnection(address,port);
    try {
      tcp.connect();
      udp.connect();
    } catch (ServerNotFoundException exception) {
      throw exception;
    } catch (SocketIoException exception) {
      throw exception;
    } catch (ConnectionNotAllowedException exception) {
      throw exception;
    } catch (InvalidConnectionConfigurationException exception) {
      throw exception;
    }
    setGameState(true);
    forwardingQueue.clear();
    receivingQueue.clear();
    startReceivingGameData();
    startPinging();
  }

  @Override
  public void shutdown() {
    dataReceiver.shutdownNow();
    transferWorker.shutdownNow();
    outgoingPing.shutdownNow();
  }

  @Override
  public void update(String data) {
    sendData(data);
  }

  private void setGameState(Boolean gameState) {
    checkGameStateLock.writeLock().lock();
    this.gameState.set(gameState);
    checkGameStateLock.writeLock().unlock();
  }

  private boolean checkGameState() {
    boolean result;
    checkGameStateLock.readLock().lock();
    result = gameState.get();
    checkGameStateLock.readLock().unlock();
    return result;
  }

  private void sendData(String data) {
    try {
      tcp.send(data);
    } catch (SocketIoException exception) {
      // This should never happen when using TCP.
      setGameState(false);
      exception.printStackTrace();
      setChanged();
      notifyObservers();
    }
  }

  private void startPinging() {
    Runnable ping = new Runnable() {
      @Override
      public void run() {
        while (checkGameState()) {
          try {
            udp.send(playerId.toString());
          } catch (SocketIoException exception) {
            setGameState(false);
          }
          try {
            Thread.sleep(PING_TIME_INTERVAL_MILLISECONDS);
          } catch (InterruptedException exception) {
            if (checkGameState()) {
              setGameState(false);
              exception.printStackTrace();
              setChanged();
              notifyObservers(LOST_CONNECTION);
            }
            break;
          } catch (IllegalArgumentException exception) {
            // This should never happen.
            setGameState(false);
            exception.printStackTrace();
            setChanged();
            notifyObservers(UNEXPECTED_ERROR);
          }
        }
      }
    };
    outgoingPing.execute(ping);
  }

  private void startReceivingGameData() {
    /*
     * Get data from the TCP connection and enqueue it.
     */
    dataReceiver.execute(new Runnable() {
      @Override
      public void run() {
        String data = null;
        while (checkGameState()) {
          try {
            data = tcp.read();
          } catch (SocketIoException exception) {
              if (checkGameState()) {
              setGameState(false);
              setChanged();
              notifyObservers(LOST_CONNECTION);
              break;
            }
          }
          if (data != null) {
            receivingQueue.add(data);
          } else {
            // EOF.
            setGameState(false);
          }
        }
      }
    });

    /*
     * Transfer data between queues.
     */
    transferWorker.execute(new Runnable() {
      @Override
      public void run() {
        String data = null;
        while (checkGameState() || !receivingQueue.isEmpty()) {
          try {
            data = receivingQueue.poll(SERVER_TIMEOUT_SECONDS,SECONDS);
          } catch (InterruptedException exception) {
            if (checkGameState()) {
              setGameState(false);
              exception.printStackTrace();
              setChanged();
              notifyObservers(UNEXPECTED_ERROR);
            }
            break;
          }
          if (data != null) {
            forwardingQueue.add(data);
            // Notify if the queue used for forwading was consumed.
            if (forwardingQueue.size() == 1) {
              setChanged();
              notifyObservers();
            }
          } else {
            // When there was nothing to poll from the queue.
            if (checkGameState()) {
              setGameState(false);
              setChanged();
              notifyObservers(SERVER_TIMEOUT);
            }
          }
        }
      }
    });
  }
}
