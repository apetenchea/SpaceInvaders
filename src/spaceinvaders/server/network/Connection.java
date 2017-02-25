package spaceinvaders.server.network;

import static java.util.logging.Level.SEVERE;

import com.google.gson.JsonSyntaxException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.TransferQueue;
import java.util.logging.Logger;
import spaceinvaders.command.Command;
import spaceinvaders.command.CommandDirector;
import spaceinvaders.command.server.ServerCommandBuilder;
import spaceinvaders.exceptions.CommandNotFoundException;
import spaceinvaders.server.network.senderchain.SenderChain;
import spaceinvaders.server.network.senderchain.TcpChain;
import spaceinvaders.server.network.senderchain.UdpChain;
import spaceinvaders.utility.Service;
import spaceinvaders.utility.ServiceState;

/** Handles the connection with a client. */
public class Connection implements Service<Void> {
  private static final Logger LOGGER = Logger.getLogger(Connection.class.getName());

  private final Socket socket;
  private final BufferedReader reader;
  private final TransferQueue<Command> incomingCommandQueue = new LinkedTransferQueue<>();
  private final TransferQueue<DatagramPacket> outgoingPacketQueue;
  private final CommandDirector director = new CommandDirector(new ServerCommandBuilder());
  private final ServiceState state = new ServiceState();
  private SenderChain sender;

  /**
   * @param socket an already opened TCP socket.
   * @param outgoingPacketQueue used for sending UDP packets.
   *
   * @throws IOException if the {@code socket} is not connected or an exception occurs when
   *     opening the I/O streams.
   * @throws NullPointerException if an argument is {@code null}.
   */
  public Connection(Socket socket, TransferQueue<DatagramPacket> outgoingPacketQueue)
      throws IOException {
    if (socket == null || outgoingPacketQueue == null) {
      throw new NullPointerException();
    }
    this.socket = socket;
    this.outgoingPacketQueue = outgoingPacketQueue;
    if (!socket.isConnected()) {
      throw new IOException();
    }
    reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    sender = new TcpChain(new PrintWriter(socket.getOutputStream()));
    state.set(true);
  }

  /**
   * Start reading from the TCP socket.
   *
   * @throws IOException if an error or EOF is reached while reading.
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
        break;
      }
      if (data == null) {
        // EOF.
        throw new IOException();
      }
      try {
        director.makeCommand(data);
        if (!incomingCommandQueue.offer(director.getCommand())) {
          // This should never happen.
          throw new AssertionError();
        }
      } catch (JsonSyntaxException | CommandNotFoundException exception) {
        LOGGER.log(SEVERE,exception.toString(),exception);
      }
    }
    return null;
  }

  @Override
  public void shutdown() {
    state.set(false);
    try {
      socket.close();
    } catch (IOException ioException) {
      LOGGER.log(SEVERE,ioException.toString(),ioException);
    }
  }

  /**
   * Unwrap an UDP packet and put it in the {@code incomingCommandQueue}.
   * 
   * @throws NullPointerException if argument is {@code null}.
   */
  public void unwrapPacket(DatagramPacket packet) {
    if (packet == null) {
      throw new NullPointerException();
    }
    final String data = new String(packet.getData());

    try {
      director.makeCommand(data.trim());
      if (!incomingCommandQueue.offer(director.getCommand())) {
        throw new AssertionError();
      }
    } catch (JsonSyntaxException | CommandNotFoundException exception) {
      LOGGER.log(SEVERE,exception.toString(),exception);
    }
  }

  /**
   * Send a command to the client.
   *
   * <p>Commands are not actually send over the network until {@link #flush() flush} is called.
   *
   * @throws NullPointerException if argument is {@code null}.
   */
  public void send(Command command) {
    if (command == null) {
      throw new NullPointerException();
    }
    sender.handle(command);
  }

  /**
   * Drains the {@code incomingCommandQueue} into a list.
   *
   * @return a list with the contents of {@code incomingCommandQueue}.
   */
  public List<Command> readCommands() {
    final List<Command> commands = new ArrayList<>();
    try {
      incomingCommandQueue.drainTo(commands);
    } catch (Exception exception) {
      // Do not close the connection.
      LOGGER.log(SEVERE,exception.toString(),exception);
    }
    return commands;
  }

  public boolean isClosed() {
    return socket.isClosed();
  }

  public SocketAddress getRemoteSocketAddress() {
    return socket.getRemoteSocketAddress();
  }

  /** Flush all commands from all network senders. */
  public void flush() {
    for (SenderChain it = sender; it != null; it = it.getNext()) {
      it.flush();
    }
  }

  /**
   * Add an UDP sender to the chain.
   * 
   * @param port the port of the remote client, where UDP packets should be sent.
   */
  public void setUdpChain(int port) {
    final SenderChain temp = sender;
    sender = new UdpChain(new InetSocketAddress(socket.getInetAddress(),port),outgoingPacketQueue);
    sender.setNext(temp);
  }
}
