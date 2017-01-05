package spaceinvaders.exceptions;

import java.io.IOException;

/**
 * Thrown when the socket disconnects unexpectedly.
 */
@SuppressWarnings("serial")
public class SocketDisconnectedException extends IOException {
  private static final String MESSAGE = "Connection closed unexpectedly!";

  public SocketDisconnectedException() {
    super(MESSAGE);
  }

  public SocketDisconnectedException(Throwable cause) {
    super(MESSAGE,cause);
  }
}

