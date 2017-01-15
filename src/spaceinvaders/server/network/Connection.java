package spaceinvaders.server.network;

import static spaceinvaders.exceptions.AssertionsEnum.BOUNDED_TRANSFER_QUEUE;

import java.net.DatagramPacket;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.TransferQueue;
import java.util.logging.Logger;

/** Network connection with a client. */
public class Connection {
  private static final Logger LOGGER = Logger.getLogger(Connection.class.getName());

  private final Socket socket;
  private final TransferQueue<String> incomingQueue = new LinkedTransferQueue<>();
  private final TransferQueue<DatagramPacket> outgoingQueue;

  /**
   * Construct a connection that uses the <code>socket</code> for TCP and
   * <code>outgoingQueue</code> to forward UDP packets.
   *
   * @throws NullPointerException - if any of the arguments is <code>null</code>.
   */
  public Connection(Socket socket, TransferQueue<DatagramPacket> outgoingQueue) {
    if (socket == null || outgoingQueue == null) {
      throw new NullPointerException();
    }
    this.socket = socket;
    this.outgoingQueue = outgoingQueue;
  }

  public void unwrapPacket(DatagramPacket packet) {
    if (!incomingQueue.offer(new String(packet.getData()))) {
      throw new AssertionError(BOUNDED_TRANSFER_QUEUE);
    }

    LOGGER.fine("New packet to " + hashCode() + ": " + new String(packet.getData()));
  }

  public boolean isClosed() {
    return socket.isClosed();
  }

  public SocketAddress getRemoteSocketAddress() {
    return socket.getRemoteSocketAddress();
  }
}
