package spaceinvaders.client.network;

import static java.util.logging.Level.SEVERE;

import java.io.IOException;
import java.net.SocketException;
import java.net.DatagramSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TransferQueue;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Logger;
import spaceinvaders.client.ClientConfig;
import spaceinvaders.command.Command;
import spaceinvaders.exceptions.IllegalPortNumberException;
import spaceinvaders.exceptions.SocketOpeningException;
import spaceinvaders.utility.Service;
import spaceinvaders.utility.ServiceState;
import spaceinvaders.utility.Chain;

/**
 * Network connection with the server.
 *
 * <p>Establishes a connection and handles I/O operations.
 */
public class NetworkConnection implements Service<Void> {
  private static final Logger LOGGER = Logger.getLogger(NetworkConnection.class.getName());

  private final Socket tcpSocket;
  private final DatagramSocket incomingUdpSocket;
  private final DatagramSocket outgoingUdpSocket;
  private final Chain<Command> sender;
  private final Service<Void> tcpReceiver;
  private final Service<Void> udpReceiver;
  private final ExecutorService tcpReceiverExecutor;
  private final ExecutorService udpReceiverExecutor;
  private final ServiceState state = new ServiceState();

  /**
   * Configure a new network connection.
   *
   * @param incomingQueue used for transfering incoming data.
   *
   * @throws SocketOpeningException if a socket could not be opened.
   * @throws IllegalPortNumberException if the port parameter is not a valid port value.
   * @throws NullPointerException if argument is {@code null}.
   * @throws SecurityException if a security manager doesn't allow an operation.
   */
  public NetworkConnection(TransferQueue<String> incomingQueue) throws SocketOpeningException {
    ClientConfig config = ClientConfig.getInstance();
    if (incomingQueue == null) {
      throw new NullPointerException();
    }
    try {
      tcpSocket = new Socket(config.getServerAddr(),config.getServerPort());
    } catch (IOException ioException) {
      throw new SocketOpeningException(ioException);
    } catch (IllegalArgumentException illegalArgException) {
      throw new IllegalPortNumberException(illegalArgException);
    }
    // Bind outgoing UDP socket to the same local address as the TCP socket.
    SocketAddress bindAddr = tcpSocket.getLocalSocketAddress();
    if (bindAddr == null) {
      // This should never happen.
      throw new AssertionError();
    }
    try {
      outgoingUdpSocket = new DatagramSocket(bindAddr);
      outgoingUdpSocket.connect(tcpSocket.getRemoteSocketAddress());
      incomingUdpSocket = new DatagramSocket();
    } catch (SocketException socketException) {
      throw new SocketOpeningException(socketException);
    }
    config.setUdpIncomingPort(incomingUdpSocket.getLocalPort());
    Chain<Command> tcpSender = null;
    Chain<Command> udpSender = null;
    try {
      tcpSender = new TcpSender(tcpSocket);
      tcpReceiver = new TcpReceiver(tcpSocket,incomingQueue);
      udpSender = new UdpSender(outgoingUdpSocket);
      udpReceiver = new UdpReceiver(incomingUdpSocket,incomingQueue);
    } catch (IOException exception) {
      throw new SocketOpeningException(exception);
    }
    udpSender.setNext(tcpSender);
    sender = udpSender;
    tcpReceiverExecutor = Executors.newSingleThreadExecutor();
    udpReceiverExecutor = Executors.newSingleThreadExecutor();
    state.set(true);
  }

  /**
   * Start network I/O.
   *
   * @throws ExecutionException if an exception occurs during execution.
   * @throws InterruptedException if the service is interrupted prior to shutdown.
   * @throws RejectedExecutionException if the task cannot be executed.
   */
  @Override
  public Void call() throws ExecutionException, InterruptedException {
    List<Future<?>> future = new ArrayList<>();
    future.add(tcpReceiverExecutor.submit(tcpReceiver));
    future.add(udpReceiverExecutor.submit(udpReceiver));
    final long checkingRateMilliseconds = 500;
    while (state.get()) {
      try {
        for (Future<?> it : future) {
          if (it.isDone()) {
            state.set(false);
            it.get();
            break;
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

  /**
   * Free resources.
   *
   * <p>Close all open sockets and shutdown executors. This instance cannot be used again.
   */
  @Override
  public void shutdown() {
    state.set(false);
    try {
      tcpSocket.close();
    } catch (IOException ioException) {
      LOGGER.log(SEVERE,ioException.toString(),ioException);
    }
    incomingUdpSocket.close();
    outgoingUdpSocket.close();
    tcpReceiver.shutdown();
    udpReceiver.shutdown();
    tcpReceiverExecutor.shutdownNow();
    udpReceiverExecutor.shutdownNow();
  }

  /**
   * Send a command to the server.
   *
   * @throws NullPointerException if an argument is {@code null}.
   */
  public void send(Command command) {
    sender.handle(command);
  }
}
