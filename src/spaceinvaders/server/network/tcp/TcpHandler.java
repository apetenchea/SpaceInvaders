package spaceinvaders.server.network.tcp;

import static java.util.logging.Level.SEVERE;
import static spaceinvaders.exceptions.AssertionsEnum.BOUNDED_TRANSFER_QUEUE;

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
 * Accepts TCP connections and forwards them using a transfer queue.
 *
 * <p>Any client initially establishes a TCP connection. The socket created this way is used only
 * for the initial data exchange and not during the game.
 */
public class TcpHandler implements Service<Void> {
  private static final Logger LOGGER = Logger.getLogger(TcpHandler.class.getName());

  private final ServerSocket serverSocket;
  private final TransferQueue<Socket> socketTransferQueue;
  private final ServiceState state = new ServiceState();

  /**
   * Construct a server socket that listens on port <code>port</code> and forwards client sockets
   * throught the <code>socketTransferQueue</code>
   *
   * @throws SocketOpeningException - if an error occurs while opening the server socket.
   * @throws SecurityException - if the security manager does not allow an operation.
   * @throws IllegalPortNumberException - if the port number is not a valid value.
   * @throws NullPointerException - if the transfer queue is <code>null</code>.
   */
  public TcpHandler(int port, TransferQueue<Socket> socketTransferQueue)
      throws SocketOpeningException, IllegalPortNumberException {
    if (socketTransferQueue == null) {
      throw new NullPointerException();
    }
    this.socketTransferQueue = socketTransferQueue;
    if (port == 0) {
      throw new IllegalPortNumberException();
    }
    try {
      serverSocket = new ServerSocket(port);
    } catch (IOException ioException) {
      throw new SocketOpeningException(ioException);
    } catch (IllegalArgumentException portException) {
      throw new IllegalPortNumberException(portException);
    }
    state.set(true);
  }

  /** Start listening for new connections. */
  @Override
  public Void call() {
    while (state.get()) {
      Socket clientSocket = null;
      LOGGER.fine("Starting tcp.");
      try {
        clientSocket = serverSocket.accept();
      } catch (Exception exception) {
        // Do not stop the server.
        if (state.get()) {
          LOGGER.log(SEVERE,exception.toString(),exception);
        }
      }
      if (clientSocket != null) {
        if (!socketTransferQueue.offer(clientSocket)) {
          throw new AssertionError(BOUNDED_TRANSFER_QUEUE);
        }
      }
    }
    return null;
  }

  /**
   * Close the server socket.
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
