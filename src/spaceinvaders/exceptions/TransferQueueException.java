package spaceinvaders.exceptions;

/**
 * Thrown when an operation on a transfer queue fails.
 */
@SuppressWarnings("serial")
public class TransferQueueException extends RuntimeException {
  private static final String MESSAGE = "Failed to enqueue data for transfer!";

  public TransferQueueException() {
    super(MESSAGE);
  }

  public TransferQueueException(Throwable cause) {
    super(MESSAGE,cause);
  }
}
