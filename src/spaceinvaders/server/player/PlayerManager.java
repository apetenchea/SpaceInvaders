package spaceinvaders.server.player;

import static java.util.logging.Level.SEVERE;

import java.io.IOException;
import java.util.Observer;
import java.util.Observable;
import java.util.List;
import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.TransferQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Logger;
import spaceinvaders.command.Command;
import spaceinvaders.command.client.SetPlayerIdCommand;
import spaceinvaders.exceptions.InterruptedServiceException;
import spaceinvaders.server.network.Connection;
import spaceinvaders.utility.Service;
import spaceinvaders.utility.ServiceState;

/**
 * Manages all players.
 *
 * <p>Wraps a {@link spaceinvaders.server.network.Connection} into a 
 * {@link spaceinvaders.server.player.Player} and forwards it to the
 * {@link spaceinvaders.server.game.GameManager}.
 */
public class PlayerManager extends Observable implements Observer, Service<Void> {
  private static final Logger LOGGER = Logger.getLogger(PlayerManager.class.getName());

  private final TransferQueue<Connection> connectionQueue = new LinkedTransferQueue<>();
  private final ServiceState state = new ServiceState();

  public PlayerManager() {
    state.set(true);
  }

  @Override
  public void update(Observable observable, Object arg) {
    if (arg instanceof Connection) {
      if (!connectionQueue.offer((Connection) arg)) {
        throw new AssertionError();
      }
    } else {
      throw new AssertionError();
    }
  }

  /**
   * Get connections out of the transfer queue and create new players.
   *
   * @throws InterruptedServiceException - if the service is interrupted prior to shutdown.
   */
  @Override
  public Void call() throws InterruptedServiceException {
    final int responseTimeoutMilliseconds = 1000;
    while (state.get()) {
      Connection connection = null;
      try {
        connection = connectionQueue.take();
      } catch (InterruptedException interruptedException) {
        if (state.get()) {
          throw new InterruptedServiceException(interruptedException);
        }
      }
      if (connection == null) {
        continue;
      }
      Player player = new Player(connection);
      player.push(new SetPlayerIdCommand(player.getId()));
      try {
        Thread.sleep(responseTimeoutMilliseconds);
      } catch (InterruptedException interruptedException) {
        if (state.get()) {
          throw new InterruptedServiceException(interruptedException);
        }
      }
      List<Command> commands = player.pull();
      if (commands != null && commands.size() == 1) {
        Command command = commands.get(0);
        command.setExecutor(player);
        command.execute();
        setChanged();
        notifyObservers(player);
      } else {
        player.close();
      }
    }
    return null; 
  }

  @Override
  public void shutdown() {
    state.set(false);
  }
}
