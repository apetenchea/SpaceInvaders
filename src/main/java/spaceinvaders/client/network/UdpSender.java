package spaceinvaders.client.network;

import static java.util.logging.Level.SEVERE;
import static spaceinvaders.command.ProtocolEnum.UDP;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.logging.Logger;
import spaceinvaders.command.Command;
import spaceinvaders.utility.Chain;

/** Send commands using the UDP protocol. */
class UdpSender implements Chain<Command> {
  private static final Logger LOGGER = Logger.getLogger(UdpSender.class.getName());

  private final DatagramSocket socket;
  private Chain<Command> nextChain;

  /**
   * Construct a sender that will communicate through the open {@code socket}.
   *
   * @throws NullPointerException if the specified socket is {@code null}.
   */
  public UdpSender(DatagramSocket socket) throws IOException {
    if (socket == null) {
      throw new NullPointerException();
    }
    this.socket = socket;
  }

  /**
   * @throws NullPointerException if {@code command} is {@code null}.
   */
  @Override
  public void handle(Command command) {
    if (command == null) {
      throw new NullPointerException();
    }
    if (command.getProtocol().equals(UDP)) {
      String data = command.toJson();
      DatagramPacket packet = new DatagramPacket(data.getBytes(),data.length());
      try {
        socket.send(packet);
      } catch (Exception exception) {
        // Do not stop the game in case one packet fails.
        LOGGER.log(SEVERE,exception.toString(),exception);
      }
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
