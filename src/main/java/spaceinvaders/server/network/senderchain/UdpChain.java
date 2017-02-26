package spaceinvaders.server.network.senderchain;

import static java.util.logging.Level.SEVERE;
import static spaceinvaders.command.ProtocolEnum.UDP;

import java.net.DatagramPacket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TransferQueue;
import java.util.logging.Logger;
import spaceinvaders.command.Command;

/** Pack and send commands over UDP. */
public class UdpChain extends SenderChain {
  private static final Logger LOGGER = Logger.getLogger(UdpChain.class.getName());

  private final List<Command> buffer = new ArrayList<>();
  private final SocketAddress packetDestination;
  private final TransferQueue<DatagramPacket> outgoingQueue;

  /**
   * @param packetDestination address to which packets are sent.
   * @param outgoingQueue transfer queue for the packets.
   *
   * @throws NullPointerException if an argument is {@code null}.
   */
  public UdpChain(SocketAddress packetDestination, TransferQueue<DatagramPacket> outgoingQueue) {
    if (packetDestination == null || outgoingQueue == null) {
      throw new NullPointerException();
    }
    this.packetDestination = packetDestination;
    this.outgoingQueue = outgoingQueue;
  }

  /**
   * @throws NullPointerException if argument is {@code null}.
   */
  @Override
  public void handle(Command command) {
    if (command == null) {
      throw new NullPointerException();
    }
    if (command.getProtocol().equals(UDP)) {
      buffer.add(command);
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
    List<DatagramPacket> packets = new ArrayList<>(buffer.size());
    for (Command command : buffer) {
      String data = command.toJson();
      try {
        packets.add(new DatagramPacket(data.getBytes(),data.length(),packetDestination));
      } catch (IllegalArgumentException illegalArgException) {
        // This should never happen.
        LOGGER.log(SEVERE,illegalArgException.toString(),illegalArgException);
        throw new AssertionError();
      }
    }
    buffer.clear();
    try {
      outgoingQueue.addAll(packets);
    } catch (RuntimeException rte) {
      // This should never happen.
      LOGGER.log(SEVERE,rte.toString(),rte);
      throw new AssertionError();
    }
  }
}
