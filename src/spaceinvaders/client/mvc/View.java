package spaceinvaders.client.mvc;

import java.awt.event.ActionListener;
import java.awt.event.KeyListener;
import java.util.List;
import spaceinvaders.client.ClientConfig;
import spaceinvaders.utility.Couple;
import spaceinvaders.game.Entity;

/**
 * View component of the application.
 *
 * @see spaceinvaders.client.mvc.Controller
 * @see spaceinvaders.client.mvc.Model
 */
public interface View {
  /**
   * Add a listener for closing the application.
   */
  public void addQuitAppListener(ActionListener listener);

  /**
   * Add a listener for quitting the game.
   */
  public void addQuitGameListener(KeyListener listener);
  
  /**
   * Add a listener for starting a new game.
   */
  public void addStartGameListener(ActionListener listener);

  /**
   * Get player's configuration.
   */
  public ClientConfig getConfig();

  /**
   * Display an error message and stop the game.
   */
  public void displayError(Exception exception);

  /**
   * Add a visual entity in the game.
   */
  public void addEntity(String type, Entity body);

  /**
   * Associate the IDs of the participating players with their names.
   */
  public void setPlayerNames(List<Couple<Integer,String>> players);

  /**
   * Change the position of an entity.
   */
  public void moveEntity(int id, int newX, int newY);

  /**
   * Destroy an entity.
   */
  public void destroyEntity(int id);

  /**
   * Flush all accumulated data that has been given by the model.
   */
  public void flush();
 
  /**
   * Play game.
   */
  public void showGame();

  /**
   * Configure game.
   */
  public void showMenu();

  /**
   * Terminate all threads started by the view.
   */
  public void shutdown();
}
