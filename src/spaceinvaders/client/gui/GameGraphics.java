package spaceinvaders.client.gui;

import java.awt.event.KeyListener;
import javax.swing.JFrame;

/**
 * Display the game as the user plays it.
 */
public class GameGraphics implements GraphicalObject {
  private JFrame gameFrame;

  /**
   * Construct an empty game frame.
   */
  public GameGraphics() {
    gameFrame = new JFrame("SpaceInvaders - Game");
    gameFrame.setSize(500,500);
    gameFrame.setResizable(false);
    gameFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
  }

  @Override
  public void destroy() {
    gameFrame.dispose();
  }

  @Override
  public void hide() {
    gameFrame.setVisible(false);
  }

  @Override
  public void show() {
    gameFrame.setVisible(true);
  }

  /**
   * Add a listener for the ESC key.
   */
  public void addEscKeyListener(KeyListener listener) {
    gameFrame.addKeyListener(listener);
  }

  public JFrame getFrame() {
    return gameFrame;
  }
}
