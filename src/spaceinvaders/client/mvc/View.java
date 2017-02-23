package spaceinvaders.client.mvc;

import java.awt.event.ActionListener;
import java.awt.event.KeyListener;
import java.util.List;
import spaceinvaders.game.Entity;
import spaceinvaders.game.EntityEnum;
import spaceinvaders.utility.Couple;

/**
 * View component of the application.
 *
 * @see spaceinvaders.client.mvc.Controller
 * @see spaceinvaders.client.mvc.Model
 */
public interface View {
  /** Add a listener for starting a new game. */
  public void addStartGameListener(ActionListener listener);

  /** Add a listener for closing the application. */
  public void addQuitAppListener(ActionListener listener);

  /** Add a listener for key bindings. */
  public void addKeyListener(KeyListener listener);

  /** Set the client configuration. */
  public void setConfig();
  
  /** Display an error message. */
  public void displayError(Exception exception);

  /** Flush all changes to the view. */
  public void flush();
 
  /** Show the game frame. */
  public void showGame();

  /** Show the manu frame. */
  public void showMenu();

  /** Player starts a new game. */
  public void startGame();

  /** Player is destroyed. */
  public void gameOver();

  /** Players won. */
  public void youWon();

  /** Players lost. */
  public void youLost();

  /** Change player score, by adding {@code value}. */
  public void changeScore(int playerId, int value);

  /** Associate the IDs of players with their names. */
  public void setPlayerNames(List<Couple<Integer,String>> players);

  /** Set entities that appear in the frame. */
  public void setFrameContent(List<Entity> content);

  /** Move an entity. */
  public void moveEntity(int entityId, int newX, int newY);

  /** Spawn an entity. */
  public void spawnEntity(int id, EntityEnum type, int posX, int posY);

  /** Remove an entity. */
  public void wipeOutEntity(int id);

  /**
   * Translate in space a group of entities.
   *
   * @param type - type of the group.
   * @param offsetX - offset on X Axis.
   * @param offsetY - offset on Y Axis.
   */
  public void translateGroup(EntityEnum type, int offsetX, int offsetY);

  /** Destroy. */
  public void shutdown();
}
