package spaceinvaders.exceptions;

import java.io.IOException;

/**
 * Thrown when the socket disconnects unexpectedly.
 */
@SuppressWarnings("serial")
public class SocketDisconnectedException extends IOException {
  public SocketDisconnectedException(Throwable cause) {
    super("Connection closed unexpectedly!",cause);
  }
}

