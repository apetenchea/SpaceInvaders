package spaceinvaders.exceptions;

import java.io.IOException;

/**
 * Thrown if the output stream of a socket has an error.
 */
@SuppressWarnings("serial")
public class SocketOutputStreamException extends IOException {
  public SocketOutputStreamException(Throwable cause) {
    super("Could not retrive data from the output stream of a socket!",cause);
  }
}
