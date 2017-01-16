package spaceinvaders.client.network;

import static java.util.logging.Level.SEVERE;
import static spaceinvaders.exceptions.AssertionsEnum.BOUNDED_TRANSFER_QUEUE;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.TransferQueue;
import java.util.logging.Logger;
import spaceinvaders.utility.Service;
import spaceinvaders.utility.ServiceState;

/** Receives data over TCP and forwards it. */
class TcpReceiver implements Service<Void> {
  private static final Logger LOGGER = Logger.getLogger(TcpReceiver.class.getName());
  private final Socket socket;
  private final BufferedReader reader;
  private final TransferQueue<String> incomingQueue;
  private final ServiceState state = new ServiceState();

  /**
   * Construct a receiver that will use the socket <code>socket</code>.
   *
   * @throws IOException - if the input stream cannot be opened, or if the socket is not connected.
   * @throws NullPointerException - if any of the arguments is <code>null</code>.
   */
  public TcpReceiver(Socket socket, TransferQueue<String> incomingQueue) throws IOException {
    if (socket == null || incomingQueue == null) {
      throw new NullPointerException();
    }
    this.socket = socket;
    this.incomingQueue = incomingQueue;
    reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    state.set(true);
  }

  @Override
  public Void call() {
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
        // An empty string is put into the queue in case of and exception or EOF.
        data = "";
      }
      if (!incomingQueue.offer(data)) {
        throw new AssertionError(BOUNDED_TRANSFER_QUEUE.toString());
      }
    }
    return null;
  }

  @Override
  public void shutdown() {
    state.set(false);
  }
}
