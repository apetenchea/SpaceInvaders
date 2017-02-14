package spaceinvaders.client.mvc;

import java.awt.event.ActionListener;
import java.awt.event.KeyListener;
import java.util.List;
import spaceinvaders.client.ClientConfig;
import spaceinvaders.utility.Couple;
import spaceinvaders.game.Entity;
import spaceinvaders.game.EntityEnum;

/**
 * View component of the application.
 *
 * @see spaceinvaders.client.mvc.Controller
 * @see spaceinvaders.client.mvc.Model
 */
public interface View {
  /**
   * Add a listener for starting a new game.
   */
  public void addStartGameListener(ActionListener listener);

  /**
   * Add a listener for closing the application.
   */
  public void addQuitAppListener(ActionListener listener);

  /**
   * Add a listener for key bindings.
   */
  public void addKeyListener(KeyListener listener);
  
  /**
   * Display an error message and stop the game.
   */
  public void displayError(Exception exception);

  /**
   * Associate the IDs of the participating players with their names.
   */
  public void setPlayerNames(List<Couple<Integer,String>> players);

  /**
   * Display the current state of the view.
   */
  public void flush();

  public void quitGame();

  public void startGame();

  public void gameOver();

  public void setFrameContent(List<Entity> content);

  public void moveEntity(int entityId, int newX, int newY);

  public void spawnEntity(int id, EntityEnum type, int posX, int posY);

  public void youWon();

  public void wipeOutEntity(int id);

  public void translateGroup(EntityEnum type,  int offsetX, int offsetY);

  public void setConfig();
 
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
