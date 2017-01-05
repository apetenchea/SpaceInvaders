package spaceinvaders.exceptions;

import java.io.IOException;

/**
 * Thrown to indicate that the server's socket could not be opened.
 */
@SuppressWarnings("serial")
public class SocketOpeningException extends IOException {
  public SocketOpeningException(Throwable cause) {
    super("An error occurred while opening a socket!",cause);
  }
}
