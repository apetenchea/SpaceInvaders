package spaceinvaders.server.network;

import java.net.DatagramPacket;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TransferQueue;
import spaceinvaders.exceptions.IllegalPortNumberException;
import spaceinvaders.exceptions.SocketOpeningException;
import spaceinvaders.server.network.tcp.TcpHandler;
import spaceinvaders.server.network.udp.UdpHandler;
import spaceinvaders.server.player.PlayerManager;
import spaceinvaders.utility.Service;
import spaceinvaders.utility.ServiceState;

/**
 * Manages I/O between clients and server.
 *
 * <p>A newly opened socket is filtered and wrapped into a
 * {@link spaceinvaders.server.network.Connection}, which is forwarded to
 * {@link spaceinvaders.server.player.PlayerManager}.
 * An incoming packet goes through a dispatcher, wich forwards it to the intended receiver.
 */
public class ConnectionManager implements Service<Void> {
  private static final int MAX_CONNECTIONS = 12;

  private final ConcurrentMap<SocketAddress,Connection> addressToConnection =
       new ConcurrentHashMap<>();
  private final TransferQueue<Socket> socketQueue = new LinkedTransferQueue<>();
  private final TransferQueue<DatagramPacket> incomingPacketQueue = new LinkedTransferQueue<>();
  private final TransferQueue<DatagramPacket> outgoingPacketQueue = new LinkedTransferQueue<>();
  private final SocketWrapper connectionWrapper = new SocketWrapper(socketQueue,outgoingPacketQueue,
      addressToConnection,new CheckSeverAvailability());
  private final Service<Void> dispatcher =
      new PacketDispatcher(incomingPacketQueue,addressToConnection);
  private final ServiceState state = new ServiceState();
  private final Service<Void> tcpHandler;
  private final Service<Void> udpHandler;
  private final ExecutorService tcpExecutor;
  private final ExecutorService udpExecutor;
  private final ExecutorService connectionWrapperExecutor;
  private final ExecutorService dispatcherExecutor;

  /**
   * @param port port used for accepting new connections and receiving data.
   *
   * @throws SocketOpeningException if an error occurs while opening a socket.
   * @throws SecurityException if a security manager does not allow an operation.
   * @throws IllegalPortNumberException if the specified port number is invalid.
   */
  public ConnectionManager(int port) throws SocketOpeningException {
    tcpHandler = new TcpHandler(port,socketQueue);
    udpHandler = new UdpHandler(port,incomingPacketQueue,outgoingPacketQueue);
    tcpExecutor = Executors.newSingleThreadExecutor();
    udpExecutor = Executors.newSingleThreadExecutor();
    connectionWrapperExecutor = Executors.newSingleThreadExecutor();
    dispatcherExecutor = Executors.newSingleThreadExecutor();
    state.set(true);
  }

  /**
   * Start handling incoming connections.
   *
   * <p>Network ports are opened, and tasks are passed to executors.
   *
   * @throws ExecutionException if an exception occurs during execution.
   * @throws InterruptedException if the service is interrupted prior to shutdown.
   * @throws RejectedExecutionException if the task cannot be executed.
   */
  @Override
  public Void call() throws ExecutionException, InterruptedException {
    List<Future<?>> future = new ArrayList<>();
    future.add(tcpExecutor.submit(tcpHandler));
    future.add(udpExecutor.submit(udpHandler));
    future.add(connectionWrapperExecutor.submit(connectionWrapper));
    future.add(dispatcherExecutor.submit(dispatcher));
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
          throw new InterruptedException();
        }
        break;
      }
    }
    return null;
  }

  @Override
  public void shutdown() {
    state.set(false);
    tcpHandler.shutdown();
    udpHandler.shutdown();
    connectionWrapper.shutdown();
    dispatcher.shutdown();
    udpExecutor.shutdownNow();
    connectionWrapperExecutor.shutdownNow();
    dispatcherExecutor.shutdownNow();
    tcpExecutor.shutdownNow();
  }

  /**
   * Add a {@link spaceinvaders.server.player.PlayerManager}, to which incoming connections are
   * forwarded.
   *
   * @throws NullPointerException if the argument is {@code null}.
   */
  public void addPlayerManager(PlayerManager playerManager) {
    if (playerManager == null) {
      throw new NullPointerException();
    }
    connectionWrapper.addObserver(playerManager);
  }

  /** Does the cleanup and checks if the server can take in one more connections. */
  private class CheckSeverAvailability implements Callable<Boolean> {
    @Override
    public Boolean call() {
      doCleanup();
      return addressToConnection.size() < MAX_CONNECTIONS;
    }

    /** Clear all closed connections. */
    private void doCleanup() {
      Iterator<Connection> it = addressToConnection.values().iterator();
      while (it.hasNext()) {
        Connection connection = it.next();
        if (connection.isClosed()) {
          it.remove();
        }
      }
    }
  }
}
