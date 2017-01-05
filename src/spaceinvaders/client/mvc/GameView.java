package spaceinvaders.client.mvc;

import java.awt.event.ActionListener;
import java.awt.event.KeyListener;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import spaceinvaders.client.ClientConfig;
import spaceinvaders.client.gui.GameGraphics;
import spaceinvaders.client.gui.Menu;

/**
 * User interface for the game.
 *
 * @see spaceinvaders.client.mvc.GameController
 * @see spaceinvaders.client.mvc.GameModel
 */
public class GameView implements View {
  private GameGraphics game;
  private Menu menu;
  private Lock updateLock;
  private JFrame currentFrame;

  /**
   * Constructs a new view that is initially hidden.
   */
  public GameView() {
    menu = new Menu();
    game = new GameGraphics();
    updateLock = new ReentrantLock();
  }

  @Override
  public void addQuitAppListener(ActionListener listener) {
    menu.addQuitListener(listener);
  }

  @Override
  public void addQuitGameListener(KeyListener listener) {
    game.addEscKeyListener(listener);
  }

  @Override
  public void addStartGameListener(ActionListener listener) {
    menu.addPlayListener(listener);
  }

  @Override
  public ClientConfig getConfig() {
    return menu.getConfig();
  }

  @Override
  public void displayError(Exception exception) {
    JOptionPane.showMessageDialog(currentFrame,
        exception.toString(),
        exception.getMessage(),
        JOptionPane.ERROR_MESSAGE);
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

  @Override
  public void update(String data) {
    updateLock.lock();
    //TODO update
    updateLock.unlock();
  }
}
