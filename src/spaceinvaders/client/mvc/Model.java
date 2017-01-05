package spaceinvaders.client.mvc;

import spaceinvaders.command.Command;

/**
 * Application data.
 *
 * @see spaceinvaders.client.mvc.Controller
 * @see spaceinvaders.client.mvc.View
 */
public interface Model {
  /**
   * Add a controller which is going to interact with this model.
   */
  public void addController(Controller controller);

  /**
   * Do a specified command.
   */
  public void doCommand(Command command);

  /**
   * Prepare the model for a new game.
   */
  public void initNewGame();

  /**
   * Exit the game.
   */
  public void exitGame();

  /**
   * Stop all threads started.
   */
  public void shutdown();
}
