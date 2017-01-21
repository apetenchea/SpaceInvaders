package spaceinvaders.client.mvc;

import java.awt.event.ActionListener;
import java.awt.event.KeyListener;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import spaceinvaders.client.ClientConfig;
import spaceinvaders.client.gui.GameGraphics;
import spaceinvaders.client.gui.Menu;
import spaceinvaders.utility.Couple;
import spaceinvaders.game.Entity;

/**
 * User interface for the game.
 *
 * @see spaceinvaders.client.mvc.GameController
 * @see spaceinvaders.client.mvc.GameModel
 */
public class GameView implements View {
  private final GameGraphics game = new GameGraphics();
  private final Menu menu = new Menu();
  private JFrame currentFrame;

  /**
   * Constructs a new starting with the menu.
   */
  public GameView() {
    menu.show();
  }

  @Override
  public void addStartGameListener(ActionListener listener) {
    menu.addPlayListener(listener);
  }

  @Override
  public void addQuitAppListener(ActionListener listener) {
    menu.addQuitListener(listener);
  }

  @Override
  public void addKeyListener(KeyListener listener) {
    game.addKeyListener(listener);
  }

  @Override
  public void displayError(Exception exception) {
    JOptionPane.showMessageDialog(currentFrame,
        exception.toString(),
        exception.getMessage(),
        JOptionPane.ERROR_MESSAGE);
  }
  
  @Override
  public void setPlayerNames(List<Couple<Integer,String>> players) {
    game.setPlayerNames(players);
  }

  @Override
  public void flush() {
    game.flush();
  }

  @Override
  public void quitGame() {
    game.setMessage("Press ESC to return to menu");
  }

  @Override
  public void startGame() {
    game.setMessage("Game started");
  }

  @Override
  public void gameOver() {
    game.setMessage("Your ship has been destroyed. Press ESC to return to menu.");
  }

  @Override
  public void setFrameContent(List<Entity> content) {
    game.setFrameContent(content);
  }

  @Override
  public void setConfig() {
    menu.setConfig();
  }

  @Override
  public void showGame() {
    menu.hide();
    game.show();
    currentFrame = game.getFrame();
  }

  @Override
  public void showMenu() {
    game.hide();
    menu.show();
    currentFrame = menu.getFrame();
  }

  @Override
  public void shutdown() {
    menu.destroy();
    game.destroy();
  }
}
