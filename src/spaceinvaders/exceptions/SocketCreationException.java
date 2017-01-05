package spaceinvaders.exceptions;

import java.io.IOException;

/**
 * Thrown if an I/O error occurs when creating a socket.
 */
@SuppressWarnings("serial")
public class SocketCreationException extends IOException {
  private static final String MESSAGE = "An I/O error occured when the socket was created!";

  public SocketCreationException() {
    super(MESSAGE);
  }

  public SocketCreationException(Throwable cause) {
    super(MESSAGE,cause);
  }
}
