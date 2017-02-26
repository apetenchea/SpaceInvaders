package spaceinvaders.server.network.senderchain;

import spaceinvaders.command.Command;
import spaceinvaders.utility.Chain;

/** Send commands through different internet protocols. */
public abstract class SenderChain implements Chain<Command> {
  private SenderChain next;

  public SenderChain getNext() {
    return next;
  }

  /**
   * @throws IllegalArgumentException - if {@code next} is not of type {@code SenderChain}.
   */
  @Override
  public void setNext(Chain<Command> next) {
    if (next == null || next instanceof SenderChain) {
      this.next = (SenderChain) next;
    } else {
      throw new IllegalArgumentException();
    }
  }

  /** Flush commands. */
  public abstract void flush();
}
