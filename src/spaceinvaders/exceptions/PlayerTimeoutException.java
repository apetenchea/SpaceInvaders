package spaceinvaders.exceptions;

/**
 * Thrown to indicate failure when closing a socket.
 */
@SuppressWarnings("serial")
public class PlayerTimeoutException extends Exception {
  private static final String MESSAGE = "Player has timed out!";

  public PlayerTimeoutException() {
    super(MESSAGE);
  }

  public PlayerTimeoutException(Throwable cause) {
    super(MESSAGE,cause);
  }
}
