package spaceinvaders.exceptions;

/** Thrown when the server is not rechable. */
@SuppressWarnings("serial")
public class ServerNotFoundException extends Exception {
  private static final String MESSAGE = "The IP address of the server could not be determined!";

  public ServerNotFoundException() {
    super(MESSAGE);
  }

  public ServerNotFoundException(Throwable cause) {
    super(MESSAGE,cause);
  }
}
