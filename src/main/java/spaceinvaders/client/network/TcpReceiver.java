package spaceinvaders.client.network;

import static java.util.logging.Level.SEVERE;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.concurrent.TransferQueue;
import java.util.logging.Logger;
import spaceinvaders.utility.Service;
import spaceinvaders.utility.ServiceState;

/** Receives data throught the TCP protocol. */
class TcpReceiver implements Service<Void> {
  private static final Logger LOGGER = Logger.getLogger(TcpReceiver.class.getName());
  private final BufferedReader reader;
  private final TransferQueue<String> incomingQueue;
  private final ServiceState state = new ServiceState();

  /**
   * Construct a receiver that will communicate through the open {@code socket}.
   *
   * @param socket an open socket through which data is received.
   * @param incomingQueue a queue used to transfer incoming data.
   *
   * @throws IOException if the input stream cannot be opened, or if the socket is not connected.
   * @throws NullPointerException if an argument is {@code null}.
   */
  public TcpReceiver(Socket socket, TransferQueue<String> incomingQueue) throws IOException {
    if (socket == null || incomingQueue == null) {
      throw new NullPointerException();
    }
    this.incomingQueue = incomingQueue;
    reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    state.set(true);
  }

  /** Begin reading from the socket. */
  @Override
  public Void call() throws IOException {
    while (state.get()) {
      String data = null;
      try {
        data = reader.readLine();
      } catch (IOException ioException) {
        if (state.get()) {
          LOGGER.log(SEVERE,ioException.toString(),ioException);
        }
      }
      if (data == null) {
        /* EOF. */
        data = new String("EOF");
        state.set(false);
      }
      if (!incomingQueue.offer(data)) {
        // This should never happen.
        throw new AssertionError();
      }
    }
    return null;
  }

  @Override
  public void shutdown() {
    state.set(false);
  }
}
