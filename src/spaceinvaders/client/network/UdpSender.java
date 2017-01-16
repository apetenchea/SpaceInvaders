package spaceinvaders.client.network;

import static java.util.logging.Level.SEVERE;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.util.logging.Logger;
import spaceinvaders.command.Command;
import spaceinvaders.command.Sender;

/** Send commands over UDP. */
class UdpSender implements Sender {
  private static final Logger LOGGER = Logger.getLogger(UdpSender.class.getName());
  private final DatagramSocket socket;
  private Sender nextinChain;

  /**
   * Construct a sender that will use the socket <code>socket</code>.
   *
   * @throws NullPointerException - if the specified socket is <code>null</code>.
   */
  public UdpSender(DatagramSocket socket) throws IOException {
    if (socket == null) {
      throw new NullPointerException();
    }
    this.socket = socket;
  }

  @Override
  public void send(Command command) {
    String data = command.toJson();
    DatagramPacket packet = new DatagramPacket(data.getBytes(),data.length());
    try {
      socket.send(packet);
    } catch (Exception exception) {
      // Do not stop the game.
      LOGGER.log(SEVERE,exception.toString(),exception);
    }
  }

  @Override
  public void setNextChain(Sender next) {
    nextinChain = next;
  }
}
