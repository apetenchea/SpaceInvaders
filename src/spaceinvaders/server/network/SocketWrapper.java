package spaceinvaders.server.network;

import static java.util.logging.Level.SEVERE;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.Observable;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TransferQueue;
import java.util.logging.Logger;
import spaceinvaders.utility.Service;
import spaceinvaders.utility.ServiceState;

/**
 * Wraps a {@link Socket} into a {@link Connection}.
 *
 * <p>The resulting connection is passed to {@link PlayerManager}.
 */
class SocketWrapper extends Observable implements Service<Void> {
  private static Logger LOGGER = Logger.getLogger(SocketWrapper.class.getName());

  private final TransferQueue<Socket> socketQueue;
  private final TransferQueue<DatagramPacket> outgoingPacketQueue;
  private final ConcurrentMap<SocketAddress,Connection> addressToConnection;
  private final Callable<Boolean> checkServerAvailability;
  private final ServiceState state = new ServiceState();

  /**
   * @param socketQueue - new sockets will be taken from this queue.
   * @param outgoingPacketQueue - any packet send by a connection will be going through this queue 
   *     It is needed for contructing a {@link Connection).
   * @param addressToConnection - used for mapping socket adresses to connections.
   * @param  checkServerAvailability - checks if the server can accept an incomming connection.
   * 
   * @throws NullPointerException - if an argument is {@code null}.
   */
  public SocketWrapper(TransferQueue<Socket> socketQueue,
      TransferQueue<DatagramPacket> outgoingPacketQueue,
      ConcurrentMap<SocketAddress,Connection> addressToConnection,
      Callable<Boolean> checkServerAvailability) {
    if (socketQueue == null || addressToConnection == null || checkServerAvailability == null) {
      throw new NullPointerException();
    }
    this.socketQueue = socketQueue;
    this.outgoingPacketQueue = outgoingPacketQueue;
    this.addressToConnection = addressToConnection;
    this.checkServerAvailability = checkServerAvailability;
    state.set(true);
  }

  /**
   * @throws InterruptedException - if the service is interrupted prior to shutdown.
   */
  @Override
  public Void call() throws InterruptedException {
    final long pollTimeout = 1;
    final TimeUnit timeUnit = TimeUnit.SECONDS;

    while (state.get()) {
      Socket clientSocket = null;
      try {
        clientSocket = socketQueue.poll(pollTimeout,timeUnit);
      } catch (InterruptedException intException) {
        if (state.get()) {
          state.set(false);
          throw new InterruptedException();
        }
      }
      if (clientSocket == null) {
        continue;
      }
      boolean availability = false;
      try {
        availability = checkServerAvailability.call();
      } catch (Exception exception) {
        // This should never happen.
        throw new AssertionError();
      }
      if (availability) {
        Connection connection = null;
        try {
          connection = new Connection(clientSocket,outgoingPacketQueue);
        } catch (IOException ioException) {
          LOGGER.log(SEVERE,ioException.toString(),ioException);
          connection.shutdown();
          continue;
        }
        if (connection == null) {
          // This should never happen.
          throw new AssertionError();
        }

        LOGGER.info("New connection: " + connection.hashCode()
            + " from " + connection.getRemoteSocketAddress());

        addressToConnection.put(connection.getRemoteSocketAddress(),connection);
        setChanged();
        notifyObservers(connection);
      }
    }
    return null;
  }

  @Override
  public void shutdown() {
    state.set(false);
  }
}
