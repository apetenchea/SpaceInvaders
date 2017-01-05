package spaceinvaders.exceptions;

import java.io.IOException;

/**
 * Thrown to indicate that the server's socket could not be opened.
 */
@SuppressWarnings("serial")
public class SocketOpeningException extends IOException {
  private static final String MESSAGE = "An error occurred while opening a socket!";

  public SocketOpeningException() {
    super(MESSAGE);
  }

  public SocketOpeningException(Throwable cause) {
    super(MESSAGE,cause);
  }
}
