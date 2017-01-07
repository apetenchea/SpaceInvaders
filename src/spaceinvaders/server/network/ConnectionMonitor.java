package spaceinvaders.server.network;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Observable;
import java.util.concurrent.Callable;
import spaceinvaders.exceptions.ClosingSocketException;
import spaceinvaders.exceptions.IllegalPortNumberException;
import spaceinvaders.exceptions.ServerSocketConnectionException;
import spaceinvaders.exceptions.SocketOpeningException;
import spaceinvaders.utility.ServiceState;

/**
 * Responsible for handling new connections.
 *
 * <p>It sleeps until it receives a new connection request.
 */
public class ConnectionMonitor extends Observable implements Callable<Void> {
  private ServerSocket serverSocket;
  private ServiceState state;

  /**
   * Construct a monitor for a port.
   */
  public ConnectionMonitor(int port) throws SocketOpeningException, IllegalArgumentException {
    try {
      serverSocket = new ServerSocket(port);
    } catch (IOException exception) {
      throw new SocketOpeningException(exception);
    } catch (IllegalArgumentException exception) {
      throw new IllegalPortNumberException(exception);
    }
    state = new ServiceState(true);
  }

  @Override
  public Void call() throws ServerSocketConnectionException {
    Socket clientSocket = null;
    while (state.get()) {
      clientSocket = null;
      try {
        clientSocket = serverSocket.accept();
      } catch (IOException exception) {
        if (state.get()) {
          throw new ServerSocketConnectionException(exception);
        }
      }
      setChanged();
      notifyObservers(clientSocket);
    }
    return null;
  }

  /**
   * Close listener.
   */
  public void close() throws ClosingSocketException {
    state.set(false);
    try {
      serverSocket.close();
    } catch (IOException exception) {
      throw new ClosingSocketException(exception);
    }
  }
}
