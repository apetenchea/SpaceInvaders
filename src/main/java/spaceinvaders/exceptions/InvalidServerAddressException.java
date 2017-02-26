package spaceinvaders.exceptions;

/** Thrown when user submits an invalid server address. */
@SuppressWarnings("serial")
public class InvalidServerAddressException extends Exception {
  private static final String MESSAGE = "The provided server address is not a standard address!";

  public InvalidServerAddressException() {
    super(MESSAGE);
  }

  public InvalidServerAddressException(Throwable cause) {
    super(MESSAGE,cause);
  }
}
