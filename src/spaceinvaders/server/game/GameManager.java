package spaceinvaders.server.game;

import static java.util.logging.Level.SEVERE;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;
import spaceinvaders.exceptions.InterruptedServiceException;
import spaceinvaders.server.player.Player;
import spaceinvaders.utility.Service;
import spaceinvaders.utility.ServiceState;

/** Manages games. */
public class GameManager implements Observer, Service<Void> {
  private static final Logger LOGGER = Logger.getLogger(GameManager.class.getName());
  private static final int MAX_TEAM_SIZE = 3;

  private final List<List<Player>> teams = new ArrayList<>(MAX_TEAM_SIZE);
  private final List<Future<?>> future = new LinkedList<>();
  private final ReentrantLock futureListLock = new ReentrantLock();
  private final ServiceState state = new ServiceState();
  private final ExecutorService cachedThreadPool = Executors.newCachedThreadPool();

  public GameManager() {
    for (int index = 0; index < MAX_TEAM_SIZE; ++index) {
      teams.add(new ArrayList<Player>(index));
    }
    state.set(true);
  }

  @Override
  public void update(Observable observable, Object obj) {
    if (obj instanceof Player) {
      Player player = (Player) obj;
      int teamSize = player.getTeamSize();
      if (teamSize < 1 || teamSize > MAX_TEAM_SIZE) {
        player.close();
        return;
      }
      List<Player> team = teams.get(teamSize - 1);
      team.add(player);
      if (team.size() == teamSize) {
        teams.set(teamSize - 1,new ArrayList<Player>(teamSize));
        futureListLock.lock();
        try {
          future.add(cachedThreadPool.submit(new Game(team,cachedThreadPool)));
        } catch (Exception exception) {
          // Do not crash.
          LOGGER.log(SEVERE,exception.toString(),exception);
          for (Player it : team) {
            it.close();
          }
        }
        futureListLock.unlock();
      }
    } else {
      throw new AssertionError();
    }
  }

  /**
   * Look after running games.
   *
   * @throws InterruptedServiceException - if the service is interrupted prior to shutdown.
   */
  @Override
  public Void call() throws InterruptedServiceException {
    final long checkingRateMilliseconds = 1000;
    Iterator<Future<?>> it;
    while (state.get()) {
      futureListLock.lock();
      it = future.iterator();
      while (it.hasNext()) {
        Future<?> current = it.next();
        if (current.isDone()) {
          try {
            current.get();
          } catch (Exception exception) {
            // Do not crash.
            LOGGER.log(SEVERE,exception.toString(),exception);
          }
          it.remove();
        }
        futureListLock.unlock();
      }
      try {
        Thread.sleep(checkingRateMilliseconds);
      } catch (InterruptedException exception) {
        if (state.get()) {
          state.set(false);
          throw new InterruptedServiceException(exception);
        }
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
