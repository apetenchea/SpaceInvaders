package spaceinvaders.exceptions;

/**
 * Thrown when a service is interrupted prior to shutdown.
 */
@SuppressWarnings("serial")
public class InterruptedServiceException extends Exception {
  private static final String MESSAGE = "Service has been interrupted prior to shutdown!";

  public InterruptedServiceException() {
    super(MESSAGE);
  }

  public InterruptedServiceException(Throwable cause) {
    super(MESSAGE,cause);
  }
}
