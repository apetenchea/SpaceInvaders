package spaceinvaders.exceptions;

/** Thrown when the port number is not in the standard range. */
@SuppressWarnings("serial")
public class IllegalPortNumberException extends IllegalArgumentException {
  private static final String MESSAGE = "Port number is invalid!";

  public IllegalPortNumberException() {
    super(MESSAGE);
  }

  public IllegalPortNumberException(Throwable cause) {
    super(MESSAGE,cause);
  }
}
