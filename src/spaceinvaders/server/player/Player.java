package spaceinvaders.server.player;

import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Logger;
import spaceinvaders.command.Command;
import spaceinvaders.exceptions.InterruptedServiceException;
import spaceinvaders.server.network.Connection;
import spaceinvaders.utility.ServiceState;

/**
 * A player connected to the server.
 *
 * <p> Observers are notified when the player is no longer available.
 */
public class Player {
  private static final Logger LOGGER = Logger.getLogger(Player.class.getName());

  private final Connection connection;
  private final ExecutorService connectionExecutor;
  private final Future<Void> connectionFuture;
  private String name;
  private Integer teamSize;

  /**
   * Wrap a player around the specified connection.
   *
   * @throws RejectedExecutionException - if a task required by <code>connection</code> cannot
   *     be scheduled for execution.
   * @throws NullPointerException - if any of the arguments is <code>null</code>.
   */
  public Player(Connection connection, ExecutorService connectionExecutor) {
    if (connection == null || connectionExecutor == null) {
      throw new NullPointerException();
    }
    this.connection = connection;
    this.connectionExecutor = connectionExecutor;
    connectionFuture = connectionExecutor.submit(connection);
  }

  /**
   * Push data to the client.
   *
   * @throws NullPointerException - if the command is <code>null</code>.
   */
  public void push(Command command) {
    if (command == null) {
      throw new NullPointerException();
    }
    connection.send(command);
  }

  /**
   * Pull data.
   *
   * @return a list containing all commands received or <code>null</code> there are none.
   */
  public List<Command> pull() {
    return connection.readCommands();
  }

  /** Flush buffered commands. */
  public void flush() {
    connection.flushUdp();
  }

  /** Close connection. */
  public void close() {
    LOGGER.info("Closing connection on player: " + getId());
    connection.shutdown();
    connectionFuture.cancel(true);
  }

  public boolean isOnline() {
    return !connectionFuture.isDone();
  }

  public int getId() {
    return connection.hashCode();
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

  public void setUdpDestination(SocketAddress addr) {
    connection.setUdpDestination(addr);
  }
}
