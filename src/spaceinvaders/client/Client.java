package spaceinvaders.client;

import spaceinvaders.client.mvc.Controller;
import spaceinvaders.client.mvc.GameController;
import spaceinvaders.client.mvc.GameModel;
import spaceinvaders.client.mvc.GameView;
import spaceinvaders.client.mvc.Model;
import spaceinvaders.client.mvc.View;

/**
 * Client side of the game.
 *
 * <p>The client is the player's interface for the game. No actual game logic lies in here,
 * as it only provides the GUI.
 */
public class Client implements Runnable {
  private Controller controller;
  private Model model;
  private View userView;

  /**
   * Construct a client having the GUI as a view.
   */
  public Client() {
    model = new GameModel();
    controller = new GameController(model);
    userView = new GameView();
    controller.registerView(userView);
  }

  @Override
  public void run() {
    userView.showMenu();
  }
}
