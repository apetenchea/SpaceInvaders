package spaceinvaders.server.game;

import java.util.Collections;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import spaceinvaders.command.server.builder.ServerCommandBuilder;
import spaceinvaders.command.Command;
import spaceinvaders.command.CommandDirector;
import spaceinvaders.command.client.SetPlayerIdCommand;
import spaceinvaders.exceptions.ClosingSocketException;
import spaceinvaders.exceptions.InterruptedServiceException;
import spaceinvaders.exceptions.PlayerTimeoutException;
import spaceinvaders.server.players.Player;

/**
 * Manages game instances.
 */
public class GameManager implements Observer {
  private static final Logger LOGGER = Logger.getLogger(GameManager.class.getName());
  private static final int MAX_TEAM_SIZE = 3;

  private Map<Integer,BlockingQueue<Player>> gameQueues;

  private ExecutorService cachedThreadPool;
  
  /**
   * Construct a game manager with empty queues.
   */
  public GameManager() {
    gameQueues = new ConcurrentHashMap<Integer,BlockingQueue<Player>>();
    for (int teamSize = 1; teamSize < MAX_TEAM_SIZE; ++teamSize) {
      gameQueues.put(teamSize,new LinkedBlockingQueue<Player>());
    }
    gameQueues = Collections.unmodifiableMap(gameQueues);
    cachedThreadPool = Executors.newCachedThreadPool();
  }

  @Override
  public void update(Observable observable, Object obj) {
    if (obj instanceof Player) {
      Player player = (Player) obj;
      if (player.getState()) {
        // Player joins.
        Player joiningPlayer = (Player) obj;
        cachedThreadPool.submit(new Callable<Void>() {
          @Override
          public Void call() {
            processPlayer(joiningPlayer);
            return null;
          }
        });
      } else {
        // Player exits.
        BlockingQueue<Player> queue = gameQueues.get(player.getTeamSize());
        if (queue != null) {
          queue.remove(player);
        }
      }
    }
  }

  /**
   * Shutdown service.
   *
   * <p>All other threads started by this service are stopped.
   */
  public void shutdown() {
    cachedThreadPool.shutdownNow();
  }

  private void processPlayer(Player joiningPlayer) {
    LOGGER.info("Processing: " + joiningPlayer.hashCode());

    String data = null;
    try {
      joiningPlayer.push(new SetPlayerIdCommand(joiningPlayer.hashCode()).toJson());
      data = joiningPlayer.pull();
    } catch (InterruptedServiceException | PlayerTimeoutException exception) {
      if (joiningPlayer.getState()) {
        LOGGER.log(Level.SEVERE,exception.getMessage(),exception);
      }
      return;
    }
    // Get player name and team size.
    CommandDirector director = new CommandDirector(new ServerCommandBuilder());
    director.makeCommand(data);
    Command command = director.getCommand();
    command.setExecutor(joiningPlayer);
    command.execute();

    int teamSize = joiningPlayer.getTeamSize();
    if (teamSize < 0 || teamSize > MAX_TEAM_SIZE) {
      return;
    } 
    BlockingQueue<Player> queue = gameQueues.get(teamSize);
    if (queue == null) {
      return;
    }
    Future<Void> game = null;
    synchronized (this) {
      try {
        queue.put(joiningPlayer);
      } catch (InterruptedException exception) {
        LOGGER.log(Level.SEVERE,exception.getMessage(),exception);
        return;
      }
      if (queue.size() == joiningPlayer.getTeamSize()) {
        List<Player> players = new ArrayList<>();
        queue.drainTo(players);
        game = createGame(players);
      }
    }
    if (game == null) {
      return;
    }
    try {
      game.get();
    } catch (ExecutionException exception) {
      Exception cause = new Exception(exception.getCause());
      LOGGER.log(Level.SEVERE,cause.getMessage(),cause);
    } catch (InterruptedException exception) {
      LOGGER.log(Level.SEVERE,exception.getMessage(),exception);
    }
  }

  private Future<Void> createGame(List<Player> players) {
    LOGGER.info("Game on");
    return cachedThreadPool.submit(new Game(cachedThreadPool,players));
  }
}
