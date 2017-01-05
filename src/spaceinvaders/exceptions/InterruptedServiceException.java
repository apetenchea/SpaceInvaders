package spaceinvaders.exceptions;

/**
 * Thrown when a waiting service is interrupted.
 */
@SuppressWarnings("serial")
public class InterruptedServiceException extends Exception {
  private static final String MESSAGE = "Service has been interrupted!";

  public InterruptedServiceException() {
    super(MESSAGE);
  }

  public InterruptedServiceException(Throwable cause) {
    super(MESSAGE,cause);
  }
}
