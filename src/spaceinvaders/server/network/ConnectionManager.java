package spaceinvaders.server.network;

import java.net.Socket;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import spaceinvaders.exceptions.ClosingSocketException;
import spaceinvaders.exceptions.InterruptedServiceException;
import spaceinvaders.exceptions.SocketOpeningException;

/**
 * Manages incoming connections and packets from players.
 */
public class ConnectionManager implements Callable<Void>, Observer {
  private static final Logger LOGGER = Logger.getLogger(ConnectionManager.class.getName());

  private ConnectionMonitor connectionListener;
  private UdpListener packetListener;
  private BlockingQueue<Socket> connectionQueue;
  private BlockingQueue<String> packetQueue;

  private ExecutorService connectionListenerExecutor;
  private ExecutorService packetListenerExecutor;

  /**
   * Construct a connection manager for connections coming from the specified port.
   */
  public ConnectionManager(int port) throws SocketOpeningException {
    connectionListener = new ConnectionMonitor(port);
    packetListener = new UdpListener(port);
    connectionListener.addObserver(this);
    packetListener.addObserver(this);
    connectionQueue = new LinkedBlockingQueue<>();
    packetQueue = new LinkedBlockingQueue<>();

    connectionListenerExecutor = Executors.newSingleThreadExecutor();
    packetListenerExecutor = Executors.newSingleThreadExecutor();
  }

  @Override
  public Void call() throws Exception {
    Future<Void> connectionListenerFuture = connectionListenerExecutor.submit(connectionListener);
    Future<Void> packetListenerFuture = packetListenerExecutor.submit(packetListener);
    try {
      connectionListenerFuture.get();
      packetListenerFuture.get();
    } catch (ExecutionException exception) {
      throw new Exception(exception.getCause());
    } catch (InterruptedException exception) {
      throw new InterruptedServiceException(exception);
    }
    return null;
  }

  @Override
  public void update(Observable observable, Object data) {
    if (data instanceof Socket) {
      try {
        connectionQueue.put((Socket) data);
      } catch (InterruptedException exception) {
        LOGGER.log(Level.SEVERE,exception.getMessage(),exception);
      }
    } else if (data instanceof String) {
      try {
        packetQueue.put((String) data);
      } catch(InterruptedException exception) {
        LOGGER.log(Level.SEVERE,exception.getMessage(),exception);
      }
    }
  }

  /**
   * Shutdown service.
   *
   * <p>All other threads started by this service are stopped.
   */
  public void shutdown() {
    try {
      connectionListener.close();
    } catch (ClosingSocketException exception) {
      LOGGER.log(Level.SEVERE,exception.getMessage(),exception);
    }
    connectionListenerExecutor.shutdownNow();
    packetListener.close();
    packetListenerExecutor.shutdownNow();
  }

  public BlockingQueue<Socket> getConnectionQueue() {
    return connectionQueue;
  }

  public BlockingQueue<String> getPacketQueue() {
    return packetQueue;
  }
}
