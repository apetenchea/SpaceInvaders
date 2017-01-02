package spaceinvaders.client.network;

/**
 * I/O error while creating the socket.
 */
@SuppressWarnings("serial")
public class SocketIoException extends Exception {
  public SocketIoException() {
    super("A problem occured while communicating with the server.");
  }
}
