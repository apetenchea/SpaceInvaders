package spaceinvaders.exceptions;

import java.io.IOException;

/**
 * Thrown if the output stream of a socket has an error.
 */
@SuppressWarnings("serial")
public class SocketOutputStreamException extends IOException {
  private static final String MESSAGE =
    "Could not retrive data from the output stream of a socket!";

  public SocketOutputStreamException() {
    super(MESSAGE);
  }

  public SocketOutputStreamException(Throwable cause) {
    super(MESSAGE,cause);
  }
}
