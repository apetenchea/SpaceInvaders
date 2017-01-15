package spaceinvaders.server.network;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.logging.Level.SEVERE;
import static spaceinvaders.exceptions.AssertionsEnum.NULL_ARGUMENT;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.TransferQueue;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Logger;
import spaceinvaders.exceptions.IllegalPortNumberException;
import spaceinvaders.exceptions.InterruptedServiceException;
import spaceinvaders.exceptions.SocketOpeningException;
import spaceinvaders.server.network.tcp.TcpHandler;
import spaceinvaders.server.network.udp.UdpHandler;
import spaceinvaders.utility.Service;
import spaceinvaders.utility.ServiceState;

/** Manages I/O between clients and server. */
public class ConnectionManager extends Observable implements Service<Void> {
  private static final Logger LOGGER = Logger.getLogger(ConnectionManager.class.getName());
  private static final int MAX_CONNECTIONS = 12;

  private final Service<Void> tcpHandler;
  private final Service<Void> udpHandler;
  private final Service<Void> wrapper = new SocketWrapper();
  private final Service<Void> dispatcher = new PacketDispatcher();
  private final ReadWriteLock currentConnectionsLock = new ReentrantReadWriteLock();
  private final List<Connection> currentConnections = new LinkedList<>();
  private final TransferQueue<Socket> socketTransferQueue = new LinkedTransferQueue<>();
  private final TransferQueue<DatagramPacket> incomingPacketQueue = new LinkedTransferQueue<>();
  private final TransferQueue<DatagramPacket> outgoingPacketQueue = new LinkedTransferQueue<>();
  private final ServiceState state = new ServiceState();
  private final ExecutorService tcpExecutor;
  private final ExecutorService udpExecutor;
  private final ExecutorService wrapperExecutor;
  private final ExecutorService dispatcherExecutor;

  /**
   * Construct a connection manager that will handle I/O through the port <code>port</code>.
   *
   * @throws SocketOpeningException - if an error occurs while opening a socket.
   * @throws SecurityException - if a security manager does not allow an operation.
   * @throws IllegalPortNumberException - if the specified port number is invalid.
   */
  public ConnectionManager(int port) throws SocketOpeningException {
    try {
      tcpHandler = new TcpHandler(port,socketTransferQueue);
      udpHandler = new UdpHandler(port,incomingPacketQueue,outgoingPacketQueue);
    } catch (NullPointerException nullPtrException) {
      throw new AssertionError(NULL_ARGUMENT.toString(),nullPtrException);
    }
    tcpExecutor = Executors.newSingleThreadExecutor();
    udpExecutor = Executors.newSingleThreadExecutor();
    wrapperExecutor = Executors.newSingleThreadExecutor();
    dispatcherExecutor = Executors.newSingleThreadExecutor();
    state.set(true);
  }

  /**
   * Start network I/O.
   *
   * @throws ExecutionException - if an exception occures during execution.
   * @throws InterruptedServiceException - if the service is interrupted prior to shutdown.
   * @throws RejectedExecutionException - if the task cannot be executed.
   */
  @Override
  public Void call() throws ExecutionException, InterruptedServiceException {
    Future<Void> tcpFuture = null;
    Future<Void> udpFuture = null;
    Future<Void> wrapperFuture = null;
    Future<Void> dispatcherFuture = null;
    try {
      tcpFuture = tcpExecutor.submit(tcpHandler);
      udpFuture = udpExecutor.submit(udpHandler);
      wrapperFuture = wrapperExecutor.submit(wrapper);
      dispatcherFuture = dispatcherExecutor.submit(dispatcher);
    } catch (NullPointerException nullPtrException) {
      throw new AssertionError(NULL_ARGUMENT.toString(),nullPtrException);
    }
    LOGGER.fine("Starting connection manager.");
    final long checkingRateMilliseconds = 1000;
    while (state.get()) {
      try {
        if (tcpFuture.isDone()) {
          state.set(false);
          tcpFuture.get();
        }
        if (udpFuture.isDone()) {
          state.set(false);
          udpFuture.get();
        }
        if (wrapperFuture.isDone()) {
          state.set(false);
          wrapperFuture.get();
        }
        if (dispatcherFuture.isDone()) {
          state.set(false);
          dispatcherFuture.get();
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
    state.set(false);
    tcpHandler.shutdown();
    udpHandler.shutdown();
    wrapper.shutdown();
    dispatcher.shutdown();
    tcpExecutor.shutdownNow();
    udpExecutor.shutdown();
    wrapperExecutor.shutdown();
    dispatcherExecutor.shutdown();
  }

  private boolean checkServerAvailability() {
    currentConnectionsLock.writeLock().lock();
    Iterator<Connection> it = currentConnections.iterator();
    while (it.hasNext()) {
      Connection connection = it.next();
      if (connection.isClosed()) {
        LOGGER.fine("Cleaning : " + connection.getRemoteSocketAddress());
        it.remove();
      }
    }
    currentConnectionsLock.writeLock().unlock();
    return currentConnections.size() < MAX_CONNECTIONS;
  }

  /**
   * Used for wrapping a {@link Socket} into a {@link Connection}.
   */
  private class SocketWrapper implements Service<Void> {
    private static final int POLL_TIMEOUT_MILLISECONDS = 1000;
    private final ServiceState state = new ServiceState();

    /**
     * Start polling and wrapping sockets.
     *
     * @throws InterruptedServiceException - if the service is interrupted prior to shutdown.
     */
    @Override
    public Void call() throws InterruptedServiceException {
      state.set(true); 
      while (state.get()) {
        Socket clientSocket = null;
        try {
          clientSocket = socketTransferQueue.poll(POLL_TIMEOUT_MILLISECONDS,MILLISECONDS);
        } catch (InterruptedException interruptedException) {
          if (state.get()) {
            state.set(false);
            throw new InterruptedServiceException(interruptedException);
          }
        }
        if (clientSocket == null) {
          continue;
        }
        if (checkServerAvailability()) {
          Connection connection = null;
          try {
            connection = new Connection(clientSocket,outgoingPacketQueue);
          } catch (NullPointerException nullPtrException) {
            throw new AssertionError(NULL_ARGUMENT.toString(),nullPtrException);
          }
          if (connection == null) {
            continue;
          }

          LOGGER.info("New connection " + connection.hashCode() + " from : "
              + clientSocket.getRemoteSocketAddress());

          currentConnectionsLock.writeLock().lock();
          currentConnections.add(connection);
          currentConnectionsLock.writeLock().unlock();
          setChanged();
          notifyObservers(connection);
        } else {
          try {
            clientSocket.close();
          } catch (IOException ioException) {
            LOGGER.log(SEVERE,ioException.toString(),ioException);
          }
        }
      }
      return null;
    }

    @Override
    public void shutdown() {
      state.set(false);
    }
  }

  /**
   *  Matches an incoming UDP packet to a {@link Connection}.
   */
  private class PacketDispatcher implements Service<Void> {
    private static final int POLL_TIMEOUT_MILLISECONDS = 50;
    private final ServiceState state = new ServiceState();

    /**
     * Start dispatching incoming UDP packets.
     *
     * @throws InterruptedServiceException - if the service is interrupted prior to shutdown.
     */
    @Override
    public Void call() throws InterruptedServiceException {
      state.set(true);
      while (state.get()) {
        DatagramPacket packet = null;
        try {
          packet = incomingPacketQueue.poll(POLL_TIMEOUT_MILLISECONDS,MILLISECONDS);
        } catch (InterruptedException interruptedException) {
          if (state.get()) {
            throw new InterruptedServiceException(interruptedException);
          }
        }
        if (packet == null) {
          continue;
        }

        LOGGER.info("Got a packet from " + packet.getSocketAddress() + ".");

        currentConnectionsLock.readLock().lock();
        Iterator<Connection> it = currentConnections.iterator();
        while (it.hasNext()) {
          Connection connection = it.next();
          if (packet.getSocketAddress().equals(connection.getRemoteSocketAddress())) {
            connection.unwrapPacket(packet);
            break;
          }
        }
        currentConnectionsLock.readLock().unlock();
      }
      return null;
    }

    @Override
    public void shutdown() {
      state.set(false);
    }
  }
}
