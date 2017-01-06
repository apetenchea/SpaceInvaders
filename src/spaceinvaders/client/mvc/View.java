package spaceinvaders.client.mvc;

import java.awt.event.ActionListener;
import java.awt.event.KeyListener;
import java.util.List;
import spaceinvaders.client.ClientConfig;
import spaceinvaders.game.GameWorld;
import spaceinvaders.utility.Couple;

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
   * Set the IDs and names of the players participating in the game.
   */
  public void setPlayers(List<Couple<Integer,String>> players);

  /**
   * Initial view for the game world.
   */
  public void initGameWorld(GameWorld world);

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

  /**
   * Update the view in conformity with the model.
   */
  public void update(String data);
}
