package spaceinvaders.server.network.udp;

import static java.util.logging.Level.SEVERE;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.concurrent.TransferQueue;
import java.util.logging.Logger;
import spaceinvaders.exceptions.SocketOpeningException;
import spaceinvaders.utility.Service;
import spaceinvaders.utility.ServiceState;

/** Receives UDP packets. */
class UdpReceiver implements Service<Void> {
  private static final Logger LOGGER = Logger.getLogger(UdpReceiver.class.getName());

  private final DatagramSocket serverSocket;
  private final TransferQueue<DatagramPacket> incomingPacketQueue;
  private final Integer maxPacketSize;
  private final ServiceState state = new ServiceState();

  /**
   * @param serverSocket open socket used for receiving packets.
   * @param maxPacketSize maximum size of a packet (in bytes).
   * @param incomingPacketQueue queue to put packets after they are received.
   *
   * @throws NullPointerException if an argument is {@code null}.
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

  /**
   * Start listening for incoming UDP packets.
   *
   * @throws IOException if an I/O error occurs.
   */
  @Override
  public Void call() throws IOException {
    while (state.get()) {
      byte[] buffer = new byte[maxPacketSize];
      DatagramPacket packet = new DatagramPacket(buffer,buffer.length);
      try {
        serverSocket.receive(packet);
        if (!incomingPacketQueue.offer(packet)) {
          // This should never happen.
          throw new AssertionError();
        }
      } catch (IOException ioException) {
        if (state.get()) {
          throw ioException;
        }
      } catch (RuntimeException rti) {
        // Do not stop the server in case one packet fails.
        if (state.get()) {
          LOGGER.log(SEVERE,rti.toString(),rti);
        }
      }
    }
    return null;
  }

  /**
   * Close the socket.
   *
   * <p>No more packets can be received.
   */
  @Override
  public void shutdown() {
    state.set(false);
    serverSocket.close();
  }
}
