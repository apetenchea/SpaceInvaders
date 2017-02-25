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

  /** Make all changes take effect. */
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

  /**
   * Change player score, by adding {@code value}.
   */
  public void changeScore(int playerId, int value);

  /**
   * Associate the ids of players with their names.
   *
   * @param players a list of 2-tuples of the form {player id, player name}.
   */
  public void setPlayerNames(List<Couple<Integer,String>> players);

  /**
   * Set all entities which shall appear in the frame.
   *
   * @param content list of entities.
   */
  public void setFrameContent(List<Entity> content);

  /**
   * Move an entity.
   *
   * @param entityId id of the entity to be moved.
   * @param newX new coordinate on the x-axis.
   * @param newY new coordinate on the y-axis.
   */
  public void moveEntity(int entityId, int newX, int newY);

  /**
   * Create a new entity.
   *
   * @param id entity id.
   * @param type type of the entity.
   * @param posX x-axis coordinate of the entity.
   * @param posY y-axis coordinate of the entity.
   */
  public void spawnEntity(int id, EntityEnum type, int posX, int posY);

  /**
   * Remove an entity.
   *
   * @param id entity id.
   */
  public void wipeOutEntity(int id);

  /**
   * Translate in space a group of entities.
   *
   * @param type type of the group.
   * @param offsetX offset on x-axis.
   * @param offsetY offset on y-axis.
   */
  public void translateGroup(EntityEnum type, int offsetX, int offsetY);

  /** Free resources. */
  public void shutdown();
}
