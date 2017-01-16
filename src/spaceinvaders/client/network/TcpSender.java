package spaceinvaders.client.network;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import spaceinvaders.command.Command;
import spaceinvaders.command.Sender;

/** Send commands over TCP. */
class TcpSender implements Sender {
  private final Socket socket;
  private final PrintWriter writer;
  private Sender nextinChain;

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

  @Override
  public void send(Command command) {
    writer.println(command.toJson()); 
  }

  @Override
  public void setNextChain(Sender next) {
    nextinChain = next;
  }
}
