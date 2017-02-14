package spaceinvaders.client.network;

import static java.util.logging.Level.SEVERE;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.concurrent.TransferQueue;
import java.util.logging.Logger;
import spaceinvaders.utility.Service;
import spaceinvaders.utility.ServiceState;

/** Receive data over UDP. */
class UdpReceiver implements Service<Void> {
  private static final Logger LOGGER = Logger.getLogger(UdpReceiver.class.getName());
  private static final int MAX_PACKET_SIZE = 16 * 1024;

  private final ServiceState state = new ServiceState();
  private final DatagramSocket socket;
  private final TransferQueue<String> incomingQueue;

  /**
   * Construct a receiver that will communicate through {@code socket}.
   *
   * @throws NullPointerException - if any of the arguments is {@code null}.
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
          throw new AssertionError();
        }
      } catch (Exception exception) {
        // Do not stop the receiver in case one packet fails.
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
