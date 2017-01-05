package spaceinvaders.exceptions;

/**
 * Thrown when a waiting service is interrupted.
 */
@SuppressWarnings("serial")
public class InterruptedServiceException extends Exception {
  public InterruptedServiceException(Throwable cause) {
    super("Service has been interrupted!",cause);
  }
}
