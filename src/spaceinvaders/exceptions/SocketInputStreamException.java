package spaceinvaders.exceptions;

import java.io.IOException;

/**
 * Thrown if the input stream of a socket has an error.
 */
@SuppressWarnings("serial")
public class SocketInputStreamException extends IOException {
  private static final String MESSAGE = "Could not retrive data from the input stream of a socket!";

  public SocketInputStreamException() {
    super(MESSAGE);
  }

  public SocketInputStreamException(Throwable cause) {
    super(MESSAGE,cause);
  }
}
