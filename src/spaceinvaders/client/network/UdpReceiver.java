package spaceinvaders.client.network;

import static java.util.logging.Level.SEVERE;
import static spaceinvaders.exceptions.AssertionsEnum.BOUNDED_TRANSFER_QUEUE;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.TransferQueue;
import java.util.logging.Logger;
import spaceinvaders.utility.Service;
import spaceinvaders.utility.ServiceState;

/** Receive data over UDP. */
class UdpReceiver implements Service<Void> {
  private static final Logger LOGGER = Logger.getLogger(UdpReceiver.class.getName());
  private static final int MAX_PACKET_SIZE = 16 * 1024;

  private final DatagramSocket socket;
  private final TransferQueue<String> incomingQueue;
  private final ServiceState state = new ServiceState();

  /**
   * Construct a receiver that will use the socket <code>socket</code>.
   *
   * @throws NullPointerException - if any of the arguments is <code>null</code>.
   */
  public UdpReceiver(DatagramSocket socket, TransferQueue<String> incomingQueue) {
    if (socket == null || incomingQueue == null) {
      throw new NullPointerException();
    }
    this.socket = socket;
    this.incomingQueue = incomingQueue;
    state.set(true);
  }

  @Override
  public Void call() {
    while (state.get()) {
      byte[] buffer = new byte[MAX_PACKET_SIZE];
      DatagramPacket packet = new DatagramPacket(buffer,buffer.length);
      try {
        socket.receive(packet);
        if (!incomingQueue.offer((new String(packet.getData())).trim())) {
          throw new AssertionError(BOUNDED_TRANSFER_QUEUE.toString());
        }
      } catch (Exception exception) {
        // Do not stop the receiver.
        if (state.get()) {
          LOGGER.log(SEVERE,exception.toString(),exception);
        }
      }
    }
    return null;
  }

  @Override
  public void shutdown() {
    state.set(false);
  }
}
