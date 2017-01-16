package spaceinvaders.client.mvc;

import spaceinvaders.command.Command;
import spaceinvaders.utility.Service;

/**
 * Application logic.
 *
 * @see spaceinvaders.client.mvc.Controller
 * @see spaceinvaders.client.mvc.View
 */
public interface Model extends Service<Void> {
  /** Couple a controller */
  public void addController(Controller controller);

  /** Do a command. */
  public void doCommand(Command command);

  /** Start playing the game. */
  public void playGame();

  /** Exit the game.*/
  public void exitGame();
}
