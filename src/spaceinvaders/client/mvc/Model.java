package spaceinvaders.client.mvc;

import spaceinvaders.command.Command;
import spaceinvaders.utility.Service;

/**
 * Decouples game logic from the rest of the application.
 *
 * @see spaceinvaders.client.mvc.Controller
 * @see spaceinvaders.client.mvc.View
 */
public interface Model extends Service<Void> {
  /** Couple a controller. */
  public void addController(Controller controller);

  /** Do a command. */
  public void doCommand(Command command);

  /** Exit the game.*/
  public void exitGame();

  /**
   * @return true if the game is on, false otherwise.
   */
  public boolean getGameState();

  /**
   * Set the game state to {@code state}.
   */
  public void setGameState(boolean state);
}
