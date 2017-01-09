package spaceinvaders.client.mvc;

import java.util.List;
import java.util.Observer;
import spaceinvaders.game.Entity;
import spaceinvaders.utility.Couple;

/**
 * Separates application data and user interface.
 *
 * @see spaceinvaders.client.mvc.Model
 * @see spaceinvaders.client.mvc.View
 */
public interface Controller extends Observer {
  /**
   * Register a view with this controller. 
   *
   * @param view the view to be registered
   */
  public void registerView(View view);

  /**
   * Configure player for the start of the game.
   *
   * <p>Used in order to execute the command:
   * {@link spaceinvaders.command.client.SetPlayerIdCommand}
   */
  public void configurePlayer(int id);

  /**
   * Add an entity in the game.
   *
   * <p>Used in order to execute the command:
   * {@link spaceinvaders.command.client.AddEntityCommand}
   */
  public void addEntity(String type, Entity body);

  /**
   * Associate player names with their IDs.
   * <p>Used in order to execute the command:
   * {@link spaceinvaders.command.client.SetPlayerNamesCommand}
   */
  public void setPlayerNames(List<Couple<Integer,String>> list);

  /**
   * Make views flush their accumulated data.
   * <p>Used in order to execute the command:
   * {@link spaceinvaders.command.client.FlushScreenCommand}
   */
  public void flushViews();
}
