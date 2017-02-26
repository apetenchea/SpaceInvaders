package spaceinvaders.utility;

/**
 * Chain of Responsibility.
 *
 * <p>A pattern for organizing the execution of processing flows.
 */
public interface Chain<T> {
  /** Either handles the task or passes it to the next in chain. */
  public void handle(T task);

  /**
   * @param next next in chain.
   */
  public void setNext(Chain<T> next);
}
