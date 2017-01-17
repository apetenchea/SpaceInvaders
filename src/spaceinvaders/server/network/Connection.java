package spaceinvaders.server.network;

import static java.util.logging.Level.SEVERE;
import static spaceinvaders.command.ProtocolEnum.TCP;
import static spaceinvaders.command.ProtocolEnum.UDP;
import static spaceinvaders.exceptions.AssertionsEnum.BOUNDED_TRANSFER_QUEUE;

import com.google.gson.JsonSyntaxException;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.TransferQueue;
import java.util.logging.Logger;
import spaceinvaders.command.Command;
import spaceinvaders.command.CommandDirector;
import spaceinvaders.command.client.ClientCommandBuilder;
import spaceinvaders.exceptions.CommandNotFoundException;
import spaceinvaders.utility.Service;
import spaceinvaders.utility.ServiceState;
import spaceinvaders.utility.Sender;

/** Network connection with a client. */
public class Connection implements Service<Void> {
  private static final Logger LOGGER = Logger.getLogger(Connection.class.getName());

  private final Integer id;
  private final Socket socket;
  private final BufferedReader reader;
  private final PrintWriter writer;
  private final Sender sender = new UdpSender(new TcpSender());
  private final TransferQueue<Command> incomingQueue = new LinkedTransferQueue<>();
  private final TransferQueue<DatagramPacket> outgoingQueue;
  private final CommandDirector director = new CommandDirector(new ClientCommandBuilder());
  private final ServiceState state = new ServiceState();
  private SocketAddress udpDestination;

  /**
   * Construct a connection that uses the <code>socket</code> for TCP and
   * <code>outgoingQueue</code> to forward UDP packets.
   *
   * @throws IOException - if an exception occurs while creating the I/O streams for the socket.
   * @throws NullPointerException - if any of the arguments is <code>null</code>.
   */
  public Connection(Socket socket, TransferQueue<DatagramPacket> outgoingQueue) throws IOException {
    if (socket == null || outgoingQueue == null) {
      throw new NullPointerException();
    }
    reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    writer = new PrintWriter(socket.getOutputStream(),true);
    this.socket = socket;
    this.outgoingQueue = outgoingQueue;
    id = hashCode();
    state.set(true);
  }

  /**
   * Start reading from the TCP socket.
   *
   * @throws IOException - if an error or EOF is reached while reading.
   */
  @Override
  public Void call() throws IOException {
    while (state.get()) {
      String data = null;
      try {
        data = reader.readLine();
      } catch (IOException ioException) {
        if (state.get()) {
          throw ioException;
        }
      }
      if (data == null) {
        throw new IOException();
      }

      LOGGER.fine("New tcp to " + hashCode() + ": " + data);

      try {
        director.makeCommand(data);
        if (!incomingQueue.offer(director.getCommand())) {
          throw new AssertionError();
        }
      } catch (JsonSyntaxException jsonException) {
        LOGGER.log(SEVERE,jsonException.toString(),jsonException);
      } catch (CommandNotFoundException commandException) {
        LOGGER.log(SEVERE,commandException.toString(),commandException);
      }
    }
    return null;
  }

  /**
   * Close the connection.
   *
   * @throws SecurityException - from {@link ExecutorService#shutdown()}.
   * @throws RuntimePermission - from {@link ExecutorService#shutdown()}.
   */
  @Override
  public void shutdown() {
    state.set(false);
    try {
      socket.close();
    } catch (IOException ioException) {
      LOGGER.log(SEVERE,ioException.toString(),ioException);
    }
  }

  public void unwrapPacket(DatagramPacket packet) {
    String data = new String(packet.getData());
    try {
      director.makeCommand(data);
      if (!incomingQueue.offer(director.getCommand())) {
        throw new AssertionError();
      }
    } catch (JsonSyntaxException jsonException) {
      LOGGER.log(SEVERE,jsonException.toString(),jsonException);
    } catch (CommandNotFoundException commandException) {
      LOGGER.log(SEVERE,commandException.toString(),commandException);
    }

    LOGGER.fine("New packet to " + hashCode() + ": " + data);
  }

  public void send(Command command) {
    sender.send(command);
  }

  /** Get all commands from the input queue. */
  public List<Command> getCommands() {
    List<Command> commands = new ArrayList<>();
    try {
      incomingQueue.drainTo(commands);
    } catch (Exception exception) {
      // Do not crash.
      LOGGER.log(SEVERE,exception.toString(),exception);
    }
    if (commands.size() == 0) {
      return null;
    }
    return commands;
  }

  public boolean isClosed() {
    return socket.isClosed();
  }

  public SocketAddress getRemoteSocketAddress() {
    return socket.getRemoteSocketAddress();
  }

  public void setUdpDestination(SocketAddress udpDestination) {
    this.udpDestination = udpDestination;
  }

  /** Pack and send UDP data. */
  private class UdpSender implements Sender {
    private Sender nextChain;

    public UdpSender() {}

    public UdpSender(Sender nextChain) {
      this.nextChain = nextChain;
    }

    /**
     * @throws NullPointerException - if {@code command} is {@code null}.
     */
    @Override
    public void send(Command command) {
      if (command == null) {
        throw new NullPointerException();
      }
      if (command.getProtocol().equals(UDP)) {
        String data = command.toJson();
        try {
          DatagramPacket packet = new DatagramPacket(data.getBytes(),data.length(),udpDestination);
          if (!outgoingQueue.offer(packet)) {
            throw new AssertionError();
          }
        } catch (Exception exception) {
          LOGGER.log(SEVERE,exception.toString(),exception);
        }
      } else {
        if (nextChain == null) {
          throw new AssertionError();
        }
        nextChain.send(command);
      }
    }

    /**
     * @throws NullPointerException - if {@code nextChain} is {@code null}.
     */
    @Override
    public void setNextChain(Sender nextChain) {
      if (nextChain == null) {
        throw new NullPointerException();
      }
      this.nextChain = nextChain;
    }
  }

  /** Send over TCP. */
  private class TcpSender implements Sender {
    private Sender nextChain;

    public TcpSender() {}

    public TcpSender(Sender nextChain) {
      this.nextChain = nextChain;
    }

    /**
     * @throws NullPointerException - if {@code command} is {@code null}.
     */
    @Override
    public void send(Command command) {
      if (command == null) {
        throw new NullPointerException();
      }
      if (command.getProtocol().equals(TCP)) {
        writer.println(command.toJson());
      } else {
        if (nextChain == null) {
          throw new AssertionError();
        }
        nextChain.send(command);
      }
    }

    /**
     * @throws NullPointerException - if {@code nextChain} is {@code null}.
     */
    @Override
    public void setNextChain(Sender nextChain) {
      if (nextChain == null) {
        throw new NullPointerException();
      }
      this.nextChain = nextChain;
    }
  }
}
