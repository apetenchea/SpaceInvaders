package spaceinvaders.server.network.udp;

import static java.util.logging.Level.SEVERE;
import static spaceinvaders.exceptions.AssertionsEnum.BOUNDED_TRANSFER_QUEUE;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.concurrent.TransferQueue;
import java.util.logging.Logger;
import spaceinvaders.exceptions.SocketOpeningException;
import spaceinvaders.utility.Service;
import spaceinvaders.utility.ServiceState;

/** Receives UDP packets and enqueues them for transfer. */
class UdpReceiver implements Service<Void> {
  private static final Logger LOGGER = Logger.getLogger(UdpReceiver.class.getName());

  private final DatagramSocket serverSocket;
  private final TransferQueue<DatagramPacket> incomingPacketQueue;
  private final Integer maxPacketSize;
  private final ServiceState state = new ServiceState();

  /**
   * Construct an UDP receiver that receives packets of size <code>maxPacketSize</code> through
   * the socket <code>serverSocket</code>, forwarding them into <code>incomingPacketQueue</code>.
   *
   * @throws NullPointerException - if an argument is <code>null</code>.
   */
  public UdpReceiver(DatagramSocket serverSocket, int maxPacketSize,
      TransferQueue<DatagramPacket> incomingPacketQueue) throws SocketOpeningException {
    if (incomingPacketQueue == null || serverSocket == null) {
      throw new NullPointerException();
    }
    this.serverSocket = serverSocket;
    this.incomingPacketQueue = incomingPacketQueue;
    this.maxPacketSize = maxPacketSize;
    state.set(true);
  }

  /** Start receiving and forwarding packets. */
  @Override
  public Void call() {
    while (state.get()) {
      byte[] buffer = new byte[maxPacketSize];
      DatagramPacket packet = new DatagramPacket(buffer,buffer.length);
      try {
        serverSocket.receive(packet);
        if (!incomingPacketQueue.offer(packet)) {
          throw new AssertionError(BOUNDED_TRANSFER_QUEUE);
        }
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
   * <p>No more packets can be received by this listener.
   */
  @Override
  public void shutdown() {
    state.set(false);
    serverSocket.close();
  }
}
