package spaceinvaders.server.network.udp;

import static java.util.logging.Level.SEVERE;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.concurrent.TransferQueue;
import java.util.logging.Logger;
import spaceinvaders.utility.Service;
import spaceinvaders.utility.ServiceState;

/** Takes UDP packets out of a transfer queue and sends them to their destination. */
class UdpSender implements Service<Void> {
  private static final Logger LOGGER = Logger.getLogger(UdpSender.class.getName());

  private final DatagramSocket serverSocket;
  private final TransferQueue<DatagramPacket> outgoingPacketQueue;
  private final ServiceState state = new ServiceState();

  /**
   * Construct an UDP sender over an open socket.
   *
   * @param serverSocket socket thought which packets are sent.
   * @param outgoingPacketQueue queue from which packets are taken out before being sent.
   *
   * @throws NullPointerException if an argument is {@code null}.
   */
  public UdpSender(DatagramSocket serverSocket,
      TransferQueue<DatagramPacket> outgoingPacketQueue) {
    if (serverSocket == null || outgoingPacketQueue == null) {
      throw new NullPointerException();
    }
    this.serverSocket = serverSocket;
    this.outgoingPacketQueue = outgoingPacketQueue;
    state.set(true);
  }

  /**
   * Start polling packets from the queue and sending them.
   *
   * @throws IOException if an I/O error occurs.
   * @throws InterruptedException if the service is interrupted prior to shutdown.
   */
  @Override
  public Void call() throws IOException, InterruptedException {
    while (state.get()) {
      DatagramPacket packet = null;
      try {
        packet = outgoingPacketQueue.take();
      } catch (InterruptedException intException) {
        if (state.get()) {
          throw new InterruptedException();
        }
        break;
      }
      try {
        serverSocket.send(packet);
      } catch (IOException ioException) {
        if (state.get()) {
          throw ioException;
        }
      } catch (RuntimeException rte) {
        // Do not stop sending packets if one packet fails.
        if (state.get()) {
          LOGGER.log(SEVERE,rte.toString(),rte);
        }
      }
    }
    return null;
  }

  /** Close the socket, discarding any unsent packets. */
  @Override
  public void shutdown() {
    state.set(false);
    serverSocket.close();
  }
}
