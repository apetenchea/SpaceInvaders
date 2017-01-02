package spaceinvaders.client.network;

/**
 * The connection is not allowed, usually due to a security manager.
 */
@SuppressWarnings("serial")
public class ConnectionNotAllowedException extends Exception {
  public ConnectionNotAllowedException() {
    super("The connection is not permitted.");
  }
}
