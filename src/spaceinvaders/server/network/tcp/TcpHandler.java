package spaceinvaders.server.network.tcp;

import static java.util.logging.Level.SEVERE;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.TransferQueue;
import java.util.logging.Logger;
import spaceinvaders.exceptions.IllegalPortNumberException;
import spaceinvaders.exceptions.SocketOpeningException;
import spaceinvaders.utility.Service;
import spaceinvaders.utility.ServiceState;

/**
 * Listens for TCP connections.
 *
 * <p>Handles the arrival and transfer of all sockets opened for incoming connections.
 *
 * <p>All clients initially establish a TCP connection. TCP is used only for commands for which
 * the arrival must be guaranteed and their order of arrival matters.
 */
public class TcpHandler implements Service<Void> {
  private static final Logger LOGGER = Logger.getLogger(TcpHandler.class.getName());

  private final ServerSocket serverSocket;
  private final TransferQueue<Socket> socketTransferQueue;
  private final ServiceState state = new ServiceState();

  /**
   * @param port the port on which incoming connections are expected to arrive.
   * @param socketTransferQueue a queue through which the newly open sockets are transferred.
   *
   * @throws SocketOpeningException if an error occurs while opening the server's socket.
   * @throws SecurityException if the security manager does not allow an operation.
   * @throws IllegalPortNumberException if the port number is not valid.
   * @throws NullPointerException if an argument is {@code null}.
   */
  public TcpHandler(int port, TransferQueue<Socket> socketTransferQueue)
      throws SocketOpeningException, IllegalPortNumberException {
    if (socketTransferQueue == null) {
      throw new NullPointerException();
    }
    this.socketTransferQueue = socketTransferQueue;
    if (port == 0) {
      // Do not let the system pick a random port.
      throw new IllegalPortNumberException();
    }
    try {
      serverSocket = new ServerSocket(port);
    } catch (IOException ioException) {
      throw new SocketOpeningException(ioException);
    } catch (IllegalArgumentException portException) {
      throw new IllegalPortNumberException();
    }
    state.set(true);
  }

  /** Start listening for new connections. */
  @Override
  public Void call() throws IOException {
    while (state.get()) {
      Socket clientSocket = null;
      try {
        clientSocket = serverSocket.accept();
      } catch (IOException ioException) {
        if (state.get()) {
          throw ioException;
        }
        break;
      } catch (RuntimeException rte) {
        // Do not stop the server.
        if (state.get()) {
          LOGGER.log(SEVERE,rte.toString(),rte);
        }
      }
      if (!socketTransferQueue.offer(clientSocket)) {
        // This should never happen.
        throw new AssertionError();
      }
    }
    return null;
  }

  /**
   * Close the socket used for listening.
   *
   * <p>The handler will no longer accept incoming TCP connections.
   */
  @Override
  public void shutdown() {
    state.set(false);
    try {
      serverSocket.close();
    } catch (IOException closingException) {
      LOGGER.log(SEVERE,closingException.toString(),closingException);
    }
  }
}
