package spaceinvaders.exceptions;

import java.io.IOException;

/**
 * Thrown to indicate failure when closing a socket.
 */
@SuppressWarnings("serial")
public class ClosingSocketException extends IOException {
  public ClosingSocketException(Throwable cause) {
    super("An error occurred while closing a socket!",cause);
  }
}
