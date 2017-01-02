package spaceinvaders.client.network;

/**
 * Connection cannot succeed due to erroneous parameters.
 */
@SuppressWarnings("serial")
public class InvalidConnectionConfigurationException extends Exception {
  public InvalidConnectionConfigurationException() {
    super("Connection address or port is invalid.");
  }
}
