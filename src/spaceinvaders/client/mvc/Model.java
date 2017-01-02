package spaceinvaders.client.mvc;

import spaceinvaders.client.ClientConfig;
import spaceinvaders.client.network.ConnectionNotAllowedException;
import spaceinvaders.client.network.InvalidConnectionConfigurationException;
import spaceinvaders.client.network.ServerNotFoundException;
import spaceinvaders.client.network.SocketIoException;

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
   * Get game state updates.
   */
  public String[] getData();

  /**
   * Exit the game.
   */
  public void exitGame() throws SocketIoException;

  /**
   * Initialize the model for a new game.
   */
  public void initNewGame(ClientConfig config) throws
      ServerNotFoundException,
      SocketIoException,
      ConnectionNotAllowedException,
      InvalidConnectionConfigurationException;

  /**
   * Stop all threads started.
   */
  public void shutdown();

  /**
   * Update the model in conformity with the view.
   */
  public void update(String data);
}
