package spaceinvaders.exceptions;

import java.io.IOException;

/**
 * Thrown to indicate failure when closing a socket.
 */
@SuppressWarnings("serial")
public class ClosingSocketException extends IOException {
  private static final String MESSAGE = "An error occurred while closing a socket!";

  public ClosingSocketException() {
    super(MESSAGE);
  }

  public ClosingSocketException(Throwable cause) {
    super(MESSAGE,cause);
  }
}
