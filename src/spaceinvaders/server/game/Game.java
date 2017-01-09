package spaceinvaders.server.game;

import static spaceinvaders.game.EnumEntity.INVADER;
import static spaceinvaders.game.EnumEntity.PLAYER;
import static spaceinvaders.game.EnumEntity.SHIELD;
import static spaceinvaders.game.EnumEntity.INVADER_BULLET;
import static spaceinvaders.game.EnumEntity.PLAYER_BULLET;

import java.util.concurrent.Callable;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.logging.Level;
import java.util.logging.Logger;
import spaceinvaders.game.GameWorld;
import spaceinvaders.server.players.Player;
import spaceinvaders.utility.Couple;
import spaceinvaders.game.Entity;
import spaceinvaders.game.EnumEntity;
import spaceinvaders.command.Command;
import spaceinvaders.command.CommandDirector;
import spaceinvaders.command.CommandBuilder;
import spaceinvaders.command.server.builder.ServerCommandBuilder;
import spaceinvaders.exceptions.InterruptedServiceException;
import spaceinvaders.command.client.AddEntityCommand;
import spaceinvaders.command.client.SetPlayerNamesCommand;
import spaceinvaders.command.client.FlushScreenCommand;
import spaceinvaders.utility.ServiceState;

/**
 * Game logic and physics happen here.
 */
public class Game implements Callable<Void> {
  private static final Logger LOGGER = Logger.getLogger(Game.class.getName());
  private static final int WAITING_TIME_MILLISECONDS = 1000;

  private ExecutorService threadPool;
  private List<Player> players;
  private Map<EnumEntity,List<Entity>> entities; 
  private CommandDirector director;
  private ServiceState state;

  /**
   * Initialize game world.
   */
  public Game(ExecutorService threadPool, List<Player> players) { 
    this.players = players;
    state = new ServiceState(false);
    director = new CommandDirector(new ServerCommandBuilder());
    entities = GameWorld.getInstance().makeGame(makeIdsList());
  }

  @Override
  public Void call() {
    state.set(true);
    sendWorld();
    sendPlayerNames();
    sendToPlayers(new FlushScreenCommand());
    try {
      Thread.sleep(WAITING_TIME_MILLISECONDS);
    } catch (InterruptedException exception) {
      LOGGER.log(Level.SEVERE,exception.toString(),exception);
    }
    /*
    playGame();
    sendQuitSignal();
    */
    return null;
  }

  private void sendToPlayers(Command command) {
    for (Player player : players) {
      try {
        player.push(command.toJson());
      } catch (InterruptedServiceException exception) {
        LOGGER.log(Level.SEVERE,exception.getMessage(),exception);
      }
    }
  }

  private void sendWorld() {
    for (Map.Entry<EnumEntity,List<Entity>> entry : entities.entrySet()) {
      EnumEntity type = entry.getKey();
      for (Entity entity : entry.getValue()) {
        sendToPlayers(new AddEntityCommand(type,entity));
      }
    }
  }

  private void sendPlayerNames() {
    sendToPlayers(new SetPlayerNamesCommand(makeCouplesIdName()));
  }

  private List<Integer> makeIdsList() {
    List<Integer> ids = new ArrayList<>(players.size());
    for (Player player : players) {
      ids.add(player.hashCode());
    }
    return ids;
  }

  private List<Couple<Integer,String>> makeCouplesIdName() {
    List<Couple<Integer,String>> couples = new ArrayList<>(players.size());
    for (Player player : players) {
      couples.add(new Couple<Integer,String>(player.hashCode(),player.getName()));
    }
    return couples;
  }
}
