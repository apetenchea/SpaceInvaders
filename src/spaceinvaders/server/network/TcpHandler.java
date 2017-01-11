package spaceinvaders.server.network;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Callable;
import java.util.concurrent.TransferQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import spaceinvaders.exceptions.IllegalPortNumberException;
import spaceinvaders.exceptions.SocketOpeningException;
import spaceinvaders.exceptions.TransferQueueException;
import spaceinvaders.utility.ServiceState;

/**
 * Accepts TCP connections and forwards them using a transfer queue.
 *
 * <p>Any client initially establishes a TCP connection. The socket created this way is used only
 * for the initial data exchange and not during the game.
 */
class TcpHandler implements Callable<Void> {
  private static final Logger LOGGER = Logger.getLogger(TcpHandler.class.getName());

  private ServerSocket serverSocket;
  private TransferQueue<Socket> socketTransferQueue;
  private ServiceState state;

  /**
   * Construct a server socket that listens on the specified port number and transfers client
   * sockets using the specified transfer queue.
   *
   * @throws SocketOpeningException - if an error occurs when opening the server socket.
   * @throws SecurityException - if a security manager forbids the opening of the server socket.
   * @throws IllegalPortNumberException - if the port number is not a valid value.
   */
  public TcpHandler(int portNumber, TransferQueue<Socket> socketTransferQueue)
      throws SocketOpeningException, IllegalPortNumberException {
    if (portNumber == 0) {
      throw new IllegalPortNumberException();
    }
    try {
      serverSocket = new ServerSocket(portNumber);
    } catch (IOException ioException) {
      throw new SocketOpeningException(ioException);
    } catch (IllegalArgumentException portException) {
      throw new IllegalPortNumberException(portException);
    }
    this.socketTransferQueue = socketTransferQueue;
    state = new ServiceState();
  }

  @Override
  public Void call() {
    while (state.get()) {
      Socket clientSocket = null;
      try {
        clientSocket = serverSocket.accept();
      } catch (IOException exception) {
        if (state.get()) {
          LOGGER.log(Level.SEVERE,exception.toString(),exception);
        }
      } catch (Exception exception) {
        LOGGER.log(Level.SEVERE,exception.toString(),exception);
      }
      if (clientSocket != null) {
        if (!socketTransferQueue.offer(clientSocket)) {
          // This should never happen if the queue is unbounded.
          Exception offerException = new TransferQueueException();
          LOGGER.log(Level.SEVERE,offerException.toString(),offerException);
          try {
            clientSocket.close();
          } catch (IOException ioException) {
            LOGGER.log(Level.SEVERE,ioException.toString(),ioException);
          }
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
  public void close() {
    if (state != null) {
      state.set(false);
    }
    if (serverSocket != null && !serverSocket.isClosed()) {
      try {
        serverSocket.close();
      } catch (IOException closingException) {
        LOGGER.log(Level.SEVERE,closingException.toString(),closingException);
      }
    }   
  }
}
