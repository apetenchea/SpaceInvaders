package spaceinvaders.server.players;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Observable;
import java.util.Date;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import spaceinvaders.exceptions.ClosingSocketException;
import spaceinvaders.exceptions.InterruptedServiceException;
import spaceinvaders.exceptions.PlayerTimeoutException;
import spaceinvaders.exceptions.SocketDisconnectedException;
import spaceinvaders.exceptions.SocketInputStreamException;
import spaceinvaders.exceptions.SocketOutputStreamException;
import spaceinvaders.utility.ServiceState;

/**
 * A player connected to the server.
 */
public class Player extends Observable implements Callable<Void> {
  private static final Logger LOGGER = Logger.getLogger(Player.class.getName());
  private static final long PLAYER_TIMEOUT_SECONDS = 1000;

  private String name;
  private Integer teamSize;
  private Integer totalPings;
  private volatile Double pingFrequency;
  private Date lastPing;

  private Socket socket;
  private BufferedReader reader;
  private PrintWriter writer;

  private BlockingQueue<String> incomingQueue;
  private BlockingQueue<String> outgoingQueue;
  private Future<Void> readerFuture;
  private Future<Void> writerFuture;
  private ServiceState state;

  private ExecutorService ioExecutor;

  /**
   * Construct a player using the provided socket.
   */
  public Player(Socket socket, ExecutorService ioExecutor) throws SocketInputStreamException,
         SocketOutputStreamException {
    this.socket = socket;
    this.ioExecutor = ioExecutor;
    name = new String();
    teamSize = new Integer(0);
    totalPings = new Integer(0);
    pingFrequency = new Double(0.0);
    lastPing = new Date();

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
    readerFuture = ioExecutor.submit(new Callable<Void>() {
      @Override
      public Void call() throws SocketInputStreamException, SocketDisconnectedException,
             InterruptedServiceException {
        while (state.get()) {
          String data = null;
          try {
            data = reader.readLine();
          } catch (IOException exception) {
            if (state.get()) {
              state.set(false);
              throw new SocketInputStreamException(exception);
            }
            break;
          }
          if (data == null) {
            // EOF.
            if (state.get()) {
              state.set(false);
              throw new SocketDisconnectedException();
            }
            break;
          }
          LOGGER.info("Got " + data);
          try {
            incomingQueue.put(data);
          } catch (InterruptedException exception) {
            if (state.get()) {
              state.set(false);
              throw new InterruptedServiceException(exception);
            }
            break;
          }
          LOGGER.info(data);
        }
        return null;
      }
    });

    writerFuture = ioExecutor.submit(new Callable<Void>() {
      @Override
      public Void call() {
        while (state.get()) {
          String data = null;
          try {
            data = outgoingQueue.take();
          } catch (InterruptedException exception) {
            if (state.get()) {
              state.set(false);
              LOGGER.log(Level.SEVERE,exception.toString(),exception);
            }
            break;
          }
          writer.println(data);
        }
        return null;
      }
    });

    try {
      try {
        readerFuture.get();
        writerFuture.cancel(true);
      } catch (CancellationException exception) {
        LOGGER.warning("Suppressed " + exception);
      }
    } catch (ExecutionException exception) {
      Exception cause = new Exception(exception.getCause());
      LOGGER.log(Level.SEVERE,cause.getMessage(),cause);
    } catch (InterruptedException exception) {
      if (state.get()) {
        LOGGER.log(Level.SEVERE,exception.toString(),exception);
      }
    } finally {
      if (!socket.isClosed()) {
        try {
          socket.close();
        } catch (IOException exception) {
          LOGGER.log(Level.SEVERE,exception.getMessage(),exception);
        }
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
  public String pull() throws InterruptedServiceException, PlayerTimeoutException {
    String data = null;
    try {
      data = incomingQueue.poll(PLAYER_TIMEOUT_SECONDS,TimeUnit.SECONDS);
    } catch (InterruptedException exception) {
      throw new InterruptedServiceException(exception);
    }
    if (data == null) {
      throw new PlayerTimeoutException();
    }
    return data;
  }

  /**
   * A new ping from this player.
   */
  public void updatePingStatus() {
    ++totalPings;
    Date now = new Date();
    long timeDiff = now.getTime() - lastPing.getTime();
    lastPing = now;
    pingFrequency = (pingFrequency > 0 ? (pingFrequency * 3 + timeDiff) / 4 : 1);
  }

  /**
   * Close connection.
   */
  public void close() throws ClosingSocketException {
    state.set(false);
    if (readerFuture != null) {
      readerFuture.cancel(true);
    }
    if (writerFuture != null) {
      writerFuture.cancel(true);
    }
    if (!socket.isClosed()) {
      try {
        socket.close();
      } catch (IOException exception) {
        throw new ClosingSocketException(exception);
      }
    }
    setChanged();
    notifyObservers();
  }

  public double getPingFrequency() {
    return pingFrequency;
  }

  public boolean getState() {
    return state.get();
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public int getTeamSize() {
    return teamSize;
  }

  public void setTeamSize(int teamSize) {
    this.teamSize = teamSize;
  }
}
