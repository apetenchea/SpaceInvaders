package spaceinvaders.exceptions;

/** Thrown when user submits an invalid invalid user name. */
@SuppressWarnings("serial")
public class InvalidUserNameException extends Exception {
  private static final String MESSAGE =
      "User name should start with a letter and contain between 2 and 10 letters or digits!";

  public InvalidUserNameException() {
    super(MESSAGE);
  }

  public InvalidUserNameException(Throwable cause) {
    super(MESSAGE,cause);
  }
}
