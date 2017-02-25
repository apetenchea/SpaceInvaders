package spaceinvaders.client.network;

import static spaceinvaders.command.ProtocolEnum.TCP;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import spaceinvaders.command.Command;
import spaceinvaders.utility.Chain;

/** Send commands throught the TCP protocol. */
class TcpSender implements Chain<Command> {
  private final PrintWriter writer;
  private Chain<Command> nextChain;

  /**
   * Construct a sender that will use the open {@code socket}.
   *
   * @throws IOException if the output stream cannot be created or if the socket is
   *     not connected.
   * @throws NullPointerException if argument is {@code null}.
   */
  public TcpSender(Socket socket) throws IOException {
    if (socket == null) {
      throw new NullPointerException();
    }
    writer = new PrintWriter(socket.getOutputStream(),true);
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
      if (nextChain == null) {
        // This should never happen.
        throw new AssertionError();
      }
      nextChain.handle(command);
    }
  }

  @Override
  public void setNext(Chain<Command> nextChain) {
    this.nextChain = nextChain;
  }
}
