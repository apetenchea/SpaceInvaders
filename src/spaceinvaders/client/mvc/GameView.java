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
  private GameGraphics game;
  private Menu menu;
  private JFrame currentFrame;

  /**
   * Constructs a new view that is initially hidden.
   */
  public GameView() {
    menu = new Menu();
    game = new GameGraphics();
  }

  @Override
  public void addQuitAppListener(ActionListener listener) {
    menu.addQuitListener(listener);
  }

  @Override
  public void addQuitGameListener(KeyListener listener) {
    game.addKeyListener(listener);
  }

  @Override
  public void addMoveLeftListener(KeyListener listener) {
    game.addKeyListener(listener);
  }

  @Override
  public void addMoveRightListener(KeyListener listener) {
    game.addKeyListener(listener);
  }

  @Override
  public void addShootListener(KeyListener listener) {
    game.addKeyListener(listener);
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
  public void addEntity(String type, Entity body) {
    game.addEntity(type,body);
  }

  @Override
  public void destroyEntity(int id) {
    game.destroyEntity(id);
  }

  @Override
  public void setPlayerNames(List<Couple<Integer,String>> players) {
    game.setPlayerNames(players);
  }

  @Override
  public void moveEntity(int id, int newX, int newY) {
    game.moveEntity(id,newX,newY);
  }

  @Override
  public void flush() {
    game.flush();
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
