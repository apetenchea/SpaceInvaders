package spaceinvaders.exceptions;

import java.io.IOException;

/**
 * Thrown if an error occurs while waiting for a client connection.
 */
@SuppressWarnings("serial")
public class ServerSocketConnectionException extends IOException {
  private static final String MESSAGE =
    "An error occurred while the server was waiting for new connections!";

  public ServerSocketConnectionException() {
    super(MESSAGE);
  }

  public ServerSocketConnectionException(Throwable cause) {
    super(MESSAGE,cause);
  }
}
