package spaceinvaders.server.network;

import java.net.DatagramPacket;
import java.net.SocketAddress;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TransferQueue;
import spaceinvaders.utility.Service;
import spaceinvaders.utility.ServiceState;

/**
 * Matches an incoming UDP packet to a {@link spaceinvaders.server.network.Connection}.
 *
 * <p>When an UDP packet arrives, it goes through the dispatcher and it is forwarded to the
 * corresponding {@link spaceinvaders.server.network.Connection}, based on the address it was sent
 * from.
 */
class PacketDispatcher implements Service<Void> {
  private final TransferQueue<DatagramPacket> packetQueue;
  private final ConcurrentMap<SocketAddress,Connection> addressToConnection;
  private final ServiceState state = new ServiceState();

  /**
   * @param packetQueue incoming packets will be taken from this queue.
   * @param addressToConnection each socket address is mapped to a connection.
   *
   * @throws NullPointerException if an argument is {@code null}.
   */
  public PacketDispatcher(TransferQueue<DatagramPacket> packetQueue,
      ConcurrentMap<SocketAddress,Connection> addressToConnection) {
    if (packetQueue == null || addressToConnection == null) {
      throw new NullPointerException();
    }
    this.packetQueue = packetQueue;
    this.addressToConnection = addressToConnection;
    state.set(true);
  }

  /**
   * Start dispatching incoming packets.
   *
   * @throws InterruptedException if the service is interrupted prior to shutdown.
   */
  @Override
  public Void call() throws InterruptedException {
    while (state.get()) {
      DatagramPacket packet = null;
      try {
        packet = packetQueue.take();
      } catch (InterruptedException intException) {
        if (state.get()) {
          state.set(false);
          throw new InterruptedException();
        }
        break;
      }
      if (packet == null) {
        // This should never happen.
        throw new AssertionError();
      }

      SocketAddress addr = packet.getSocketAddress();
      Connection receiver = addressToConnection.get(addr);
      if (receiver != null) {
        receiver.unwrapPacket(packet);
      }
    }
    return null;
  }

  @Override
  public void shutdown() {
    state.set(false);
  }
}
