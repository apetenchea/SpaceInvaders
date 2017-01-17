package spaceinvaders.command;

import spaceinvaders.command.Command;

/**
 * Chain of responsability for sending commands over different network protocols.
 *
 * <p>If a sender cannot handle the protocol over which the command must be sent, it passes
 * the data to the next sender in chain.
 */
public interface Sender {
  /** Either sends the command or passes it to the next sender in the chain. */
  public void send(Command command);

  /** If this sender cannot handle the protocol, pass it to <code>next</code>. */
  public void setNextChain(Sender nextChain);
}
