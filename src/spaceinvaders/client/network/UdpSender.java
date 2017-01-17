package spaceinvaders.client.network;

import static java.util.logging.Level.SEVERE;
import static spaceinvaders.command.ProtocolEnum.UDP;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.logging.Logger;
import spaceinvaders.command.Command;
import spaceinvaders.utility.Sender;

/** Send commands over UDP. */
class UdpSender implements Sender {
  private static final Logger LOGGER = Logger.getLogger(UdpSender.class.getName());
  private final DatagramSocket socket;
  private Sender nextChain;

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

  /**
   * @throws NullPointerException - if <code>command</code> is <code>null</code>.
   */
  @Override
  public void send(Command command) {
    if (command == null) {
      throw new NullPointerException();
    }
    if (command.getProtocol() != UDP) {
      if (nextChain == null) {
        throw new AssertionError();
      }
      nextChain.send(command);
    }
    String data = command.toJson();
    DatagramPacket packet = new DatagramPacket(data.getBytes(),data.length());
    try {
      socket.send(packet);
    } catch (Exception exception) {
      // Do not stop the game.
      LOGGER.log(SEVERE,exception.toString(),exception);
    }
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
