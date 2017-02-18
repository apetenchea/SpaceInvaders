package spaceinvaders.exceptions;

/** Thrown to indicate an unrecognized command. */
@SuppressWarnings("serial")
public class CommandNotFoundException extends Exception {
  private static final String MESSAGE = "Command not found!";

  public CommandNotFoundException() {
    super(MESSAGE);
  }

  public CommandNotFoundException(Throwable cause) {
    super(MESSAGE,cause);
  }
}

