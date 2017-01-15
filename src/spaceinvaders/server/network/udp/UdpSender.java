package spaceinvaders.server.network.udp;

import static java.util.logging.Level.SEVERE;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.concurrent.TransferQueue;
import java.util.logging.Logger;
import spaceinvaders.exceptions.SocketOpeningException;
import spaceinvaders.utility.Service;
import spaceinvaders.utility.ServiceState;

/** Takes enqued UDP packets and sends them to their destination. */
class UdpSender implements Service<Void> {
  private static final Logger LOGGER = Logger.getLogger(UdpSender.class.getName());

  private final DatagramSocket serverSocket;
  private final TransferQueue<DatagramPacket> outgoingPacketQueue;
  private final ServiceState state = new ServiceState();

  /**
   * Construct an UDP sender that takes packets from the <code>outgoingPacketQueue</code> and
   * forwards them throught the socket <code>serverSocket</code>.
   *
   * @throws NullPointerException - if an argument is <code>null</code>.
   */
  public UdpSender(DatagramSocket serverSocket, TransferQueue<DatagramPacket> outgoingPacketQueue)
      throws SocketOpeningException {
    if (outgoingPacketQueue == null || serverSocket == null) {
      throw new NullPointerException();
    }
    this.serverSocket = serverSocket;
    this.outgoingPacketQueue = outgoingPacketQueue;
    state.set(true);
  }

  /** Start taking packets out of the queue and send them. */
  @Override
  public Void call() {
    while (state.get()) {
      DatagramPacket packet = null;
      try {
        packet = outgoingPacketQueue.take();
        if (packet == null) {
          continue;
        }
        serverSocket.send(packet);
      } catch (Exception exception) {
        // Do not stop the server.
        if (state.get()) {
          LOGGER.log(SEVERE,exception.toString(),exception);
        }
      }
    }
    return null;
  }

  /**
   * Close the server socket.
   *
   * <p>Packets left in the transfer queue ar discarded.
   */
  @Override
  public void shutdown() {
    state.set(false);
    serverSocket.close();
  }
}
