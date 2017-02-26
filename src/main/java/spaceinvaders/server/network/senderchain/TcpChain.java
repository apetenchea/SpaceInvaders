package spaceinvaders.server.network.senderchain;

import static spaceinvaders.command.ProtocolEnum.TCP;

import java.io.PrintWriter;
import spaceinvaders.command.Command;

/** Send commands over TCP. */
public class TcpChain extends SenderChain {
  private final PrintWriter writer;

  /**
   * @param writer writes data to the socket.
   *
   * @throws NullPointerException if argument is {@code null}.
   */
  public TcpChain(PrintWriter writer) {
    if (writer == null) {
      throw new NullPointerException();
    }
    this.writer = writer;
  }

  /**
   * @throws NullPointerException if argument is {@code null}.
   */
  @Override
  public void handle(Command command) {
    if (command == null) {
      throw new NullPointerException();
    }
    if (command.getProtocol().equals(TCP)) {
      writer.println(command.toJson());     
    } else {
      if (getNext() == null) {
        // This should never happen.
        throw new AssertionError();
      }
      getNext().handle(command);
    }
  }

  @Override
  public void flush() {
    writer.flush();
  }
}
