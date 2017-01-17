package spaceinvaders.client.network;

import static spaceinvaders.command.ProtocolEnum.TCP;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import spaceinvaders.command.Command;
import spaceinvaders.utility.Sender;

/** Send commands over TCP. */
class TcpSender implements Sender {
  private final Socket socket;
  private final PrintWriter writer;
  private Sender nextChain;

  /**
   * Construct a sender that will use the socket <code>socket</code>.
   *
   * @throws IOException - if the output stream cannot be created or if the socket is
   *     not connected.
   * @throws NullPointerException - if the specified socket is <code>null</code>.
   */
  public TcpSender(Socket socket) throws IOException {
    if (socket == null) {
      throw new NullPointerException();
    }
    this.socket = socket;
    writer = new PrintWriter(socket.getOutputStream(),true);
  }

  /**
   * @throws NullPointerException - if <code>command</code> is <code>null</code>.
   */
  @Override
  public void send(Command command) {
    if (command == null) {
      throw new NullPointerException();
    }
    if (command.getProtocol() != TCP) {
      if (nextChain == null) {
        throw new AssertionError();
      }
      nextChain.send(command);
    }
    writer.println(command.toJson()); 
  }

  /**
   * @throws NullPointerException - if <code>nextChain</code> is <code>null</code>.
   */
  @Override
  public void setNextChain(Sender nextChain) {
    if (nextChain == null) {
      throw new NullPointerException();
    }
    this.nextChain = nextChain;
  }
}
