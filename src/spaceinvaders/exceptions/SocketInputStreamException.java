package spaceinvaders.exceptions;

import java.io.IOException;

/**
 * Thrown if the input stream of a socket has an error.
 */
@SuppressWarnings("serial")
public class SocketInputStreamException extends IOException {
  public SocketInputStreamException(Throwable cause) {
    super("Could not retrive data from the input stream of a socket!",cause);
  }
}
