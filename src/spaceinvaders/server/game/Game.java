package spaceinvaders.server.game;

import static spaceinvaders.game.EntityEnum.INVADER;
import static spaceinvaders.game.EntityEnum.PLAYER;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.logging.Logger;
import spaceinvaders.command.Command;
import spaceinvaders.command.client.FlushScreenCommand;
import spaceinvaders.command.client.GameOverCommand;
import spaceinvaders.command.client.PlayersWonCommand;
import spaceinvaders.command.client.QuitGameCommand;
import spaceinvaders.command.client.RefreshEntitiesCommand;
import spaceinvaders.command.client.SetPlayerNamesCommand;
import spaceinvaders.command.client.StartGameCommand;
import spaceinvaders.server.game.world.ClassicWorldBuilder;
import spaceinvaders.server.game.world.World;
import spaceinvaders.server.game.world.WorldDirector;
import spaceinvaders.server.player.Player;
import spaceinvaders.utility.Couple;
import spaceinvaders.utility.Service;
import spaceinvaders.utility.ServiceState;

/**
 * The actual gameplay.
 *
 * <p>All game logic and physics happen here.
 */
class Game implements Service<Void> {
  private static final Logger LOGGER = Logger.getLogger(Game.class.getName());
  private static final int FRAMES_PER_SECOND = 20;
  private static final boolean PREDICTABLE_GAME = false;

  private final ServiceState state = new ServiceState();
  private final List<Player> team;
  private final World world;
  private final Random rng;
  private final GameLoop gameLoop;

  /**
   * Create a new game.
   *
   * @param team - players joining this game.
   * @param threadPool - used to for game threads.
   *
   * @throws NullPointerException - if any of the arguments is {@code null}
   */
  public Game(List<Player> team, ExecutorService threadPool) {
    if (team == null || threadPool == null) {
      throw new NullPointerException();
    }
    this.team = team;

    /* Build world. */
    WorldDirector director = new WorldDirector(new ClassicWorldBuilder());
    List<Integer> idList = new ArrayList<>(team.size());
    for (int index = 0; index < idList.size(); ++index) {
      idList.set(index,team.get(index).getId());
    }
    director.makeWorld(idList);
    world = director.getWorld();

    if (PREDICTABLE_GAME) {
      rng = new Random(1103515245);
    } else {
      rng = new Random();
    }

    gameLoop = new GameLoop(team,world,rng,threadPool);

    state.set(true);
  }

  /**
   * Play the game.
   *
   * @throws ExecutionException - if an exception occurs during execution.
   * @throws InterruptedException - if the service is interrupted prior to shutdown.
   */
  @Override
  public Void call() throws ExecutionException, InterruptedException {
    StringBuffer buf = new StringBuffer();
    for (Player player : team) {
      buf.append(player.getName() + "@" + player.getId() + " ");
    }
    LOGGER.info("Started game with players: " + buf.toString());

    List<Couple<Integer,String>> idToName = new ArrayList<>(team.size());
    for (Player player : team) {
      idToName.add(new Couple<Integer,String>(player.getId(),player.getName()));
    }
    distributeCommand(new SetPlayerNamesCommand(idToName));
    distributeCommand(new StartGameCommand());
    distributeCommand(new RefreshEntitiesCommand(world.getEntities()));
    distributeCommand(new FlushScreenCommand());
    flushCommands();

    gameLoop.call();
    try {
      int frameCounter = 0;
      while (state.get()) {
        gameLoop.processInput();
        gameLoop.update();
        for (Command command : gameLoop.drainCommands()) {
          distributeCommand(command);
        }
        if (world.count(PLAYER) == 0) {
          distributeCommand(new GameOverCommand());
          state.set(false);
        } else if (world.count(INVADER) == 0) {
          distributeCommand(new PlayersWonCommand());
          state.set(false);
        }
        /* Once every 5 seconds do a complete refresh. */
        frameCounter = (frameCounter + 1) % (FRAMES_PER_SECOND * 5);
        if (frameCounter == 0) {
          distributeCommand(new RefreshEntitiesCommand(world.getEntities()));
        }
        distributeCommand(new FlushScreenCommand());
        for (Player player : team) {
          player.flush();
        }
        Thread.sleep(1000 / FRAMES_PER_SECOND);
      }
    } catch (InterruptedException intException) {
      if (state.get()) {
        throw new InterruptedException();
      }
    } finally {
      distributeCommand(new QuitGameCommand());
      shutdown();
    }

    return null; 
  }

  @Override
  public void shutdown() {
    state.set(false);
    for (Player it : team) {
      it.close();
    }
    gameLoop.shutdown();
  }

  /**
   * Forward {@code command} to the players.
   *
   * @throws NullPointerException - if the argument is {@code null}.
   */
  private void distributeCommand(Command command) {
    if (command == null) {
      throw new NullPointerException();
    }
    for (Player player : team) {
      player.push(command);
    }
  }

  private void flushCommands() {
    for (Player player : team) {
      player.flush();
    }
  }
}
