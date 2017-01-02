package spaceinvaders.client.network;

/**
 * Server address is not reachable.
 */
@SuppressWarnings("serial")
public class ServerNotFoundException extends Exception {
  public ServerNotFoundException() {
    super("The address of the server could not be determined.");
  }
}
