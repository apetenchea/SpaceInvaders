package spaceinvaders.client.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import spaceinvaders.client.ClientConfig;
import spaceinvaders.exceptions.ClosingSocketException;
import spaceinvaders.exceptions.InterruptedServiceException;
import spaceinvaders.exceptions.ServerNotFoundException;
import spaceinvaders.exceptions.SocketCreationException;
import spaceinvaders.exceptions.SocketInputStreamException;
import spaceinvaders.exceptions.SocketOutputStreamException;
import spaceinvaders.utility.ServiceState;

/**
 * Network connection used to communicate with the game server.
 *
 * @see spaceinvaders.server.Server
 */
public class NetworkConnection implements Callable<Void> {
  private static final Logger LOGGER = Logger.getLogger(NetworkConnection.class.getName());
  private ClientConfig config;
  private Socket tcpSocket;
  private ServiceState state;

  private BufferedReader reader;
  private PrintWriter printer;
  private BlockingQueue<String> readingQueue;
  private BlockingQueue<String> printingQueue;

  private ExecutorService readingExecutor;
  private ExecutorService printingExecutor;

  /**
   * Construct a network connection that will use the provided configuration.
   */
  public NetworkConnection(ClientConfig config, BlockingQueue<String> readingQueue,
      BlockingQueue<String> printingQueue) {
    this.config = config;
    this.readingQueue = readingQueue;
    this.printingQueue = printingQueue;
    state = new ServiceState(false);

    readingExecutor = Executors.newSingleThreadExecutor();
    printingExecutor = Executors.newSingleThreadExecutor();
  }

  @Override
  public Void call() throws Exception {
    try {
      tcpSocket = new Socket(config.getServerAddr(),config.getServerPort());
    } catch (UnknownHostException exception) {
      throw new ServerNotFoundException(exception);     
    } catch (IOException exception) {
      throw new SocketCreationException(exception);
    }
    try {
      reader = new BufferedReader(new InputStreamReader(tcpSocket.getInputStream()));
    } catch (IOException exception) {
      throw new SocketInputStreamException(exception);
    }
    try {
      printer = new PrintWriter(tcpSocket.getOutputStream(),true);
    } catch (IOException exception) {
      throw new SocketOutputStreamException(exception);
    }
    state.set(true);

    Future<Void> readingExecutorFuture = readingExecutor.submit(new Callable<Void>() {
      @Override
      public Void call() throws SocketInputStreamException, InterruptedServiceException {
        while (state.get()) {
          String data = null;
          try {
            data = reader.readLine();
          } catch (IOException exception) {
            if (state.get()) {
              throw new SocketInputStreamException(exception);
            }
            break;
          }
          if (data == null) {
            // EOF.
            state.set(false);
            break;
          }
          try {
            readingQueue.put(data);
          } catch (InterruptedException exception) {
            if (state.get()) {
              throw new InterruptedServiceException(exception);
            }
            break;
          }
        }
        return null;
      }
    }); 

    Future<Void> printingExecutorFuture = printingExecutor.submit(new Callable<Void>() {
      @Override
      public Void call() {
        while (state.get()) {
          String data = null;
          try {
            data = printingQueue.take();
          } catch (InterruptedException exception) {
            if (state.get()) {
              LOGGER.log(Level.SEVERE,exception.toString(),exception);
            }
            break;
          }
          printer.println(data);
        }
        return null;
      }
    });

    try {
      readingExecutorFuture.get();
      printingExecutorFuture.cancel(true);
    } catch (ExecutionException exception) {
      throw new Exception(exception.getCause());
    } catch (InterruptedException exception) {
      if (state.get()) {
        throw new InterruptedServiceException(exception);
      }
    }
    return null;
  }

  /**
   * Close connection.
   */
  public void close() throws ClosingSocketException {
    state.set(false);
    if (tcpSocket != null && !tcpSocket.isClosed()) {
      try {
        tcpSocket.close();
      } catch (IOException exception) {
        throw new ClosingSocketException(exception);
      }
    }
  }

  /**
   * Shutdown service.
   *
   * <p>Stops workers.
   */
  public void shutdown() {
    readingExecutor.shutdownNow();
    printingExecutor.shutdownNow();
  }
}
