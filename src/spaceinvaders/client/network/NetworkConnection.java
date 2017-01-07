package spaceinvaders.client.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
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
import spaceinvaders.exceptions.SocketOpeningException;
import spaceinvaders.exceptions.SocketOutputStreamException;
import spaceinvaders.utility.ServiceState;

/**
 * Network connection used to communicate with the game server.
 *
 * @see spaceinvaders.server.Server
 */
public class NetworkConnection implements Callable<Void> {
  private static final Logger LOGGER = Logger.getLogger(NetworkConnection.class.getName());
  private static final int PING_TIME_INTERVAL_MILLISECONDS = 1000; 

  private ClientConfig config;
  private ServiceState state;
  private Socket tcpSocket;
  private DatagramSocket udpSocket;

  private BufferedReader reader;
  private PrintWriter printer;
  private BlockingQueue<String> readingQueue;
  private BlockingQueue<String> printingQueue;
  private Future<Void> readingExecutorFuture;
  private Future<Void> printingExecutorFuture;
  private Future<Void> pingExecutorFuture;

  private ExecutorService readingExecutor;
  private ExecutorService printingExecutor;
  private ExecutorService pingExecutor;

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
    pingExecutor = Executors.newSingleThreadExecutor();
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

    readingExecutorFuture = readingExecutor.submit(new Callable<Void>() {
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
          LOGGER.info(data);
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

    printingExecutorFuture = printingExecutor.submit(new Callable<Void>() {
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
    } catch (CancellationException exception) {
      LOGGER.warning("Suppressing " + exception.toString());
    }
    return null;
  }

  public void startUdp() throws SocketOpeningException, ServerNotFoundException {
    try {
      udpSocket = new DatagramSocket();
    } catch (SocketException exception) {
      throw new SocketOpeningException(exception);
    }
    InetAddress ipAddress = null;
    try {
      ipAddress = InetAddress.getByName(config.getServerAddr());
    } catch (UnknownHostException exception) {
      throw new ServerNotFoundException(exception);
    }
    byte[] data = Integer.valueOf(config.getId()).toString().getBytes();

    DatagramPacket packet = new DatagramPacket(data,data.length,ipAddress,config.getServerPort());
    pingExecutorFuture = pingExecutor.submit(new Callable<Void>() {
      @Override
      public Void call() {
        while (state.get()) {
          try {
            udpSocket.send(packet);
          } catch (IOException exception) {
            if (state.get()) {
              LOGGER.log(Level.SEVERE,exception.toString(),exception);
            }
          }
          try {
            Thread.sleep(PING_TIME_INTERVAL_MILLISECONDS);
          } catch (InterruptedException exception) {
            if (state.get()) {
              LOGGER.log(Level.SEVERE,exception.toString(),exception);
            }
          }
        }
        return null;       
      }
    });
  }

  /**
   * Close connection.
   */
  public void close() throws ClosingSocketException {
    state.set(false);
    if (readingExecutorFuture != null) {
      readingExecutorFuture.cancel(true);
    }
    if (printingExecutorFuture != null) {
      printingExecutorFuture.cancel(true);
    }
    if (pingExecutorFuture != null) {
      pingExecutorFuture.cancel(true);
    }
    if (udpSocket != null && !udpSocket.isClosed()) {
      udpSocket.close();
    }
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
    pingExecutor.shutdownNow();
  }
}
