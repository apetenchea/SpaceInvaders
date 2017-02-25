package spaceinvaders.client;

import java.util.logging.Logger;
import spaceinvaders.client.mvc.Controller;
import spaceinvaders.client.mvc.GameController;
import spaceinvaders.client.mvc.GameModel;
import spaceinvaders.client.mvc.GameView;
import spaceinvaders.client.mvc.Model;
import spaceinvaders.client.mvc.View;
import spaceinvaders.utility.Service;

/**
 * Client side of the game.
 *
 * <p>The client is the player's interface for the game. No actual game logic lies in here,
 * as it only provides the GUI and the connection to the server.
 */
public class Client implements Service<Void> {
  private static final Logger LOGGER = Logger.getLogger(Client.class.getName());

  private final Controller controller;
  private final Model model;
  private final View userView;

  /** Assemble the MVC. */
  public Client() {
    model = new GameModel();
    controller = new GameController(model);
    userView = new GameView();
    controller.registerView(userView);
  }

  /** Display the start of the application (the menu). */
  @Override
  public Void call() {
    userView.showMenu();
    return null;
  }

  @Override
  public void shutdown() {
    LOGGER.info("Client is shutting down.");
  } 
}
