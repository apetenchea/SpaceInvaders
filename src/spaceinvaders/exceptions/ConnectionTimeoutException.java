package spaceinvaders.exceptions;

/**
 * Thrown when the connection times out.
 */
@SuppressWarnings("serial")
public class ConnectionTimeoutException extends Exception {
  private static final String MESSAGE = "The connection has timed out!"; 

  public ConnectionTimeoutException() {
    super(MESSAGE);
  }

  public ConnectionTimeoutException(Throwable cause) {
    super(MESSAGE,cause);
  }
}
