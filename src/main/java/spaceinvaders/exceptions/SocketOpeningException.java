package spaceinvaders.exceptions;

import java.io.IOException;

/** Thrown to indicate that a socket could not be opened. */
@SuppressWarnings("serial")
public class SocketOpeningException extends IOException {
  private static final String MESSAGE = "Socket could not be opened!";

  public SocketOpeningException() {
    super(MESSAGE);
  }

  public SocketOpeningException(Throwable cause) {
    super(MESSAGE,cause);
  }
}
