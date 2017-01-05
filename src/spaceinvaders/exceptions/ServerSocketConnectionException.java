package spaceinvaders.exceptions;

import java.io.IOException;

/**
 * Thrown if an error occurs while waiting for a client connection.
 */
@SuppressWarnings("serial")
public class ServerSocketConnectionException extends IOException {
  public ServerSocketConnectionException(Throwable cause) {
    super("An error occurred while the server was waiting for new connections!",cause);
  }
}
