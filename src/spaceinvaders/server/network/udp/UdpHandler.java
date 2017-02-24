package spaceinvaders.server.network.udp;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TransferQueue;
import spaceinvaders.exceptions.SocketOpeningException;
import spaceinvaders.utility.Service;
import spaceinvaders.utility.ServiceState;

/**
 * Handles I/O through the UDP protocol.
 *
 * <p>UDP is used for repetitive commands during the game.
 */
public class UdpHandler implements Service<Void> {
  private static final int MAX_INCOMING_PACKET_SIZE = 256;

  private final Service<Void> receiver;
  private final Service<Void> sender;
  private final ExecutorService receiverExecutor;
  private final ExecutorService senderExecutor;
  private final ServiceState state = new ServiceState();

  /**
   * Constuct an UDP handler which will start a sender and a receiver.
   *
   * <p>The port for receiving packets must be a valid one. The system automatically finds a port
   * available for sending packets. 
   *
   * @param port local port throught which packets are received.
   * @param incomingPacketQueue queue to put the received packets.
   * @param outgoingPacketQueue packets are taken out of this queue and sent.
   *
   * @throws SocketOpeningException if a server socket could not be opened or it cannot be
   *     bound to the specified local port.
   * @throws SecurityException if a security manager does not allow an operation.
   * @throws NullPointerException if an argument is {@code null}.
   */
  public UdpHandler(int port, TransferQueue<DatagramPacket> incomingPacketQueue,
      TransferQueue<DatagramPacket> outgoingPacketQueue) throws SocketOpeningException {
    if (incomingPacketQueue == null || outgoingPacketQueue == null) {
      throw new NullPointerException();
    }
    DatagramSocket receivingSocket = null;
    DatagramSocket sendingSocket = null;
    try {
      receivingSocket = new DatagramSocket(port);
      sendingSocket = new DatagramSocket();
    } catch (SocketException socketException) {
      throw new SocketOpeningException(socketException);
    }
    receiver = new UdpReceiver(receivingSocket,MAX_INCOMING_PACKET_SIZE,incomingPacketQueue);
    sender = new UdpSender(sendingSocket,outgoingPacketQueue);
    receiverExecutor = Executors.newSingleThreadExecutor();
    senderExecutor = Executors.newSingleThreadExecutor();
    state.set(true);
  }

  /**
   * Start receiving and sending packets.
   *
   * @throws ExecutionException if an exception occurs during execution.
   * @throws InterruptedException if the service is interrupted prior to shutdown.
   * @throws RejectedExecutionException if the task cannot be executed.
   */
  @Override
  public Void call() throws ExecutionException, InterruptedException {
    List<Future<?>> future = new ArrayList<>();
    future.add(receiverExecutor.submit(receiver));
    future.add(senderExecutor.submit(sender));
    final long checkingRateMilliseconds = 1000;
    while (state.get()) {
      try {
        for (Future<?> it : future) {
          if (it.isDone()) {
            state.set(false);
            it.get();
          }
        }
        Thread.sleep(checkingRateMilliseconds);
      } catch (CancellationException | InterruptedException exception) {
        if (state.get()) {
          state.set(false);
          throw new InterruptedException();
        }
        break;
      }
    }
    return null;
  }

  /** The handler will no longer be able to receive or send packets. */
  @Override
  public void shutdown() {
    state.set(false);
    receiver.shutdown();
    sender.shutdown();
    receiverExecutor.shutdownNow();
    senderExecutor.shutdownNow();
  }
}
