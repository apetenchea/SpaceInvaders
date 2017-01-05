package spaceinvaders.server.game;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Logger;

import spaceinvaders.server.players.Player;

/**
 * Manages game instances.
 */
public class GameManager implements Observer {
  private static final Logger LOGGER = Logger.getLogger(GameManager.class.getName());
  private static final int MAX_TEAM_SIZE = 3;

  private ArrayList<BlockingQueue<Player>> gameQueues;
  private ExecutorService cachedThreadPool;
  
  /**
   * Construct a game manager with empty queues.
   */
  public GameManager() {
    gameQueues = new ArrayList<BlockingQueue<Player>>(MAX_TEAM_SIZE);
    for (BlockingQueue<Player> queue : gameQueues) {
      queue = new LinkedBlockingQueue<Player>();
    }
    cachedThreadPool = Executors.newCachedThreadPool();
  }

  @Override
  public void update(Observable observable, Object obj) {
    if (obj instanceof Player) {
      Player joiningPlayer = (Player) obj;
      cachedThreadPool.submit(new Callable<Void>() {
        @Override
        public Void call() {
          processPlayer(joiningPlayer);
          return null;
        }
      });
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
    // TODO send ID
    // TODO get configuration
    // if Player.getTeamSize()
  }
}
