package spaceinvaders.server.players;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Observable;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import spaceinvaders.exceptions.ClosingSocketException;
import spaceinvaders.exceptions.InterruptedServiceException;
import spaceinvaders.exceptions.SocketDisconnectedException;
import spaceinvaders.exceptions.SocketInputStreamException;
import spaceinvaders.exceptions.SocketOutputStreamException;
import spaceinvaders.utility.ServiceState;

/**
 * A player connected to the server.
 */
public class Player extends Observable implements Callable<Void> {
  private static final Logger LOGGER = Logger.getLogger(Player.class.getName());

  private Socket socket;
  private BufferedReader reader;
  private PrintWriter writer;

  private BlockingQueue<String> incomingQueue;
  private BlockingQueue<String> outgoingQueue;
  private ExecutorService ioExecutor;
  private ServiceState state;

  /**
   * Construct a player using the provided socket.
   */
  public Player(Socket socket, ExecutorService ioExecutor) throws SocketInputStreamException,
         SocketOutputStreamException {
    this.socket = socket;
    this.ioExecutor = ioExecutor;

    try {
      reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    } catch (IOException exception) {
      throw new SocketInputStreamException(exception);
    }
    try {
      writer = new PrintWriter(socket.getOutputStream(),true);
    } catch (IOException exception) {
      throw new SocketOutputStreamException(exception);
    }

    incomingQueue = new LinkedBlockingQueue<>();
    outgoingQueue = new LinkedBlockingQueue<>();
    state = new ServiceState(true);
  }

  @Override
  public Void call() {
    Future<Void> readerFuture = ioExecutor.submit(new Callable<Void>() {
      @Override
      public Void call() throws SocketInputStreamException, SocketDisconnectedException,
             InterruptedServiceException {
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
            if (state.get()) {
              throw new SocketDisconnectedException(new IOException());
            }
            break;
          }
          try {
            incomingQueue.put(data);
          } catch (InterruptedException exception) {
            if (state.get()) {
              throw new InterruptedServiceException(exception);
            }
            break;
          }
          LOGGER.info(data);
        }
        return null;
      }
    });

    Future<Void> writerFuture = ioExecutor.submit(new Callable<Void>() {
      @Override
      public Void call() throws InterruptedServiceException {
        while (state.get()) {
          String data = null;
          try {
            data = outgoingQueue.take();
          } catch (InterruptedException exception) {
            if (state.get()) {
              throw new InterruptedServiceException(exception);
            }
          }
          writer.println(data);
        }
        return null;
      }
    });

    try {
      readerFuture.get();
      writerFuture.get();
    } catch (ExecutionException exception) {
      Exception cause = new Exception(exception.getCause());
      LOGGER.log(Level.SEVERE,cause.getMessage(),cause);
    } catch (InterruptedException exception) {
      if (state.get()) {
        LOGGER.log(Level.SEVERE,exception.toString(),exception);
      }
    } finally {
      try {
        close();
      } catch (ClosingSocketException exception) {
        LOGGER.log(Level.SEVERE,exception.getMessage(),exception);
      }
    }
    return null;
  }

  /**
   * Send data.
   */
  public void push(String data) throws InterruptedServiceException {
    try {
      outgoingQueue.put(data);
    } catch (InterruptedException exception) {
      throw new InterruptedServiceException(exception);
    }
  }

  /**
   * Get data.
   */
  public String pull() throws InterruptedServiceException  {
    String data = null;
    try {
      data = incomingQueue.take();
    } catch (InterruptedException exception) {
      throw new InterruptedServiceException(exception);
    }
    return data;
  }

  /**
   * Close connection.
   */
  public void close() throws ClosingSocketException {
    state.set(false);
    if (!socket.isClosed()) {
      setChanged();
      notifyObservers();
      try {
        socket.close();
      } catch (IOException exception) {
        throw new ClosingSocketException(exception);
      }
    }
  }
}
