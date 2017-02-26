package spaceinvaders.server.game;

import static java.util.logging.Level.SEVERE;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;
import spaceinvaders.server.player.Player;
import spaceinvaders.utility.Service;
import spaceinvaders.utility.ServiceState;

/**
 * Creates new games.
 *
 * <p>When a player is ready to play, he joins a team. If then the team has the desired size, the
 * game can start. Otherwise, he has to wait until other players join.
 */
public class GameManager implements Observer, Service<Void> {
  private static final Logger LOGGER = Logger.getLogger(GameManager.class.getName());
  private static final int MAX_TEAM_SIZE = 3;

  private final List<List<Player>> teams = new ArrayList<>(MAX_TEAM_SIZE);
  private final List<Future<?>> future = new LinkedList<>();
  private final Lock futureListLock = new ReentrantLock();
  private final ServiceState state = new ServiceState();
  private final ExecutorService cachedThreadPool = Executors.newCachedThreadPool();

  /** Construct a game manager with all teams set to 0 players. */ 
  public GameManager() {
    for (int index = 0; index < MAX_TEAM_SIZE; ++index) {
      teams.add(new ArrayList<Player>(index));
    }
    state.set(true);
  }

  /** Receive a player which is ready to join a team. */
  @Override
  public void update(Observable observable, Object obj) {
    if (!(obj instanceof Player)) {
      // This should never happen.
      throw new AssertionError();
    }
    Player player = (Player) obj;
    if (player.getTeamSize() < 1 || player.getTeamSize() > MAX_TEAM_SIZE) {
      player.close();
      return;
    }
    List<Player> team = teams.get(player.getTeamSize() - 1);
    team.add(player);

    /* Clean up the team. */
    Iterator<Player> it = team.iterator();
    while (it.hasNext()) {
      Player ply = it.next();
      if (!ply.isOnline()) {
        it.remove();
      }
    }

    if (team.size() == player.getTeamSize()) {
      teams.set(team.size() - 1,new ArrayList<Player>(team.size()));
      futureListLock.lock();
      try {
        future.add(cachedThreadPool.submit(new Game(team,cachedThreadPool)));
      } catch (Exception exception) {
        // Do not stop this thread.
        LOGGER.log(SEVERE,exception.toString(),exception);
        for (Player ply : team) {
          ply.close();
        }
      }
      futureListLock.unlock();
    }
  }

  /**
   * Continuous checking of currently running games.
   *
   * <p>When a game has ended, its resources are freed.
   *
   * @throws InterruptedException if the service is interrupted prior to shutdown.
   */
  @Override
  public Void call() throws InterruptedException {
    final long checkingRateMilliseconds = 1000;
    Iterator<Future<?>> it;
    while (state.get()) {
      futureListLock.lock();
      it = future.iterator();
      while (it.hasNext()) {
        Future<?> game = it.next();
        if (game.isDone()) {
          try {
            game.get();
          } catch (InterruptedException | CancellationException intException) {
            if (state.get()) {
              throw new InterruptedException();
            }
            break;
          } catch (ExecutionException execException) {
            // Do not stop the game manager.
            LOGGER.log(SEVERE,execException.toString(),execException);
          }
          it.remove();
        }
      }
      futureListLock.unlock();
      try {
        Thread.sleep(checkingRateMilliseconds);
      } catch (InterruptedException exception) {
        if (state.get()) {
          throw new InterruptedException();
        }
        break;
      }
    }
    return null;
  }

  @Override
  public void shutdown() {
    state.set(false);
    cachedThreadPool.shutdownNow();
  }
}
