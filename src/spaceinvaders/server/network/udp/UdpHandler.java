package spaceinvaders.server.network.udp;

import static spaceinvaders.exceptions.AssertionsEnum.NULL_ARGUMENT;

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
import java.util.concurrent.TransferQueue;

import spaceinvaders.exceptions.InterruptedServiceException;
import spaceinvaders.exceptions.SocketOpeningException;
import spaceinvaders.utility.Service;
import spaceinvaders.utility.ServiceState;

/**
 * Handles I/O using the UDP protocol.
 *
 * <p>UDP is used during the game.
 */
public class UdpHandler implements Service<Void> {
  private static final int MAX_INCOMING_PACKET_SIZE = 1024;

  private final Service<Void> receiver;
  private final Service<Void> sender;
  private final ExecutorService receiverExecutor;
  private final ExecutorService senderExecutor;
  private final ServiceState state = new ServiceState();

  /**
   * Construct an UDP handler that receives and sends packets throught the local port
   * <code>port</code>, forwarding incoming packets into  <code>incomingPacketQueue</code> and
   * sending packets taken from <code>outgoingPacketQueue</code>.
   *
   * @throws SocketOpeningException - if a server socket could not be opened or it cannot be
   *     bound to the specified local port.
   * @throws SecurityException - if a security manager does not allow an operation.
   * @throws NullPointerException - if any of the specified transfer queues is <code>null</code>.
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
   * @throws ExecutionException - if an exception occurs during execution.
   * @throws InterruptedServiceException - if the service is interrupted prior to shutdown.
   * @throws RejectedExecutionException - if the task cannot be executed.
   */
  @Override
  public Void call() throws ExecutionException, InterruptedServiceException {
    List<Future<?>> future = new ArrayList<>();
    try {
      future.add(receiverExecutor.submit(receiver));
      future.add(senderExecutor.submit(sender));
    } catch (NullPointerException nullPtrException) {
      throw new AssertionError(NULL_ARGUMENT);
    }
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
          throw new InterruptedServiceException(exception);
        }
      }
    }
    return null;
  }

  /**
   * The handler will no longer be able to receive and send packets.
   *
   * @throws SecurityException - from {@link ExecutorService#shutdown()}.
   * @throws RuntimePermission - from {@link ExecutorService#shutdown()}.
   */
  @Override
  public void shutdown() {
    state.set(false);
    receiver.shutdown();
    sender.shutdown();
    receiverExecutor.shutdownNow();
    senderExecutor.shutdownNow();
  }
}
