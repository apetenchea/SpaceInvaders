package spaceinvaders.exceptions;

/**
 * Thrown when an offer operation on a transfer queue fails.
 *
 * <p>This should never happen if the queue is unbounded.
 */
@SuppressWarnings("serial")
public class TransferQueueOfferException extends RuntimeException {
  private static final String MESSAGE = "Failed to enqueue data in the transfer queue!";

  public TransferQueueOfferException() {
    super(MESSAGE);
  }

  public TransferQueueOfferException(Throwable cause) {
    super(MESSAGE,cause);
  }
}
