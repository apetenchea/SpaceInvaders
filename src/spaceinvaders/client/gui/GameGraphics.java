package spaceinvaders.client.gui;

import java.util.List;
import java.awt.event.KeyListener;
import java.util.logging.Logger;
import java.util.logging.Level;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;


import java.awt.BorderLayout;

import javax.swing.JLabel;
import javax.swing.SwingConstants;

import spaceinvaders.game.GameConfig;
import spaceinvaders.game.Entity;
import spaceinvaders.utility.Couple;

/**
 * Display the game as the user plays it.
 */
public class GameGraphics implements GraphicalObject {
  private static final Logger LOGGER = Logger.getLogger(GameGraphics.class.getName());

  private JFrame gameFrame;
  private GamePanel gamePanel;
  private JLabel messageLbl;

  /**
   * Construct an empty game frame.
   */
  public GameGraphics() {
    GameConfig config = GameConfig.getInstance();

    gameFrame = new JFrame(config.getTitle());
    gameFrame.setResizable(false);
    gameFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    gameFrame.setSize(config.getGameFrameWidth(),config.getGameFrameHeight());

    JPanel contentPane = new JPanel();
    contentPane.setBorder(new EmptyBorder(5,5,5,5));
    contentPane.setLayout(new BorderLayout(0,0));
    gameFrame.setContentPane(contentPane);

		JPanel messagePanel = new JPanel();
		contentPane.add(messagePanel,BorderLayout.NORTH);
    messageLbl = new JLabel();
		messageLbl.setHorizontalAlignment(SwingConstants.LEFT);
		messagePanel.add(messageLbl);

    gamePanel = new GamePanel();
		gamePanel.setForeground(config.getGamePanelForegroundColor());
		contentPane.add(gamePanel,BorderLayout.CENTER);
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
    gamePanel.init();
    messageLbl.setText("Waiting for server...");
    gameFrame.setVisible(true);
  }

  public synchronized void addEntity(String type, Entity body) {
    gamePanel.addEntity(type,body);
  }

  public synchronized void destroyEntity(int id) {
    gamePanel.destroyEntity(id);
  }

  public synchronized void moveEntity(int id, int newX, int newY) {
    gamePanel.moveEntity(id,newX,newY);
  }


  public void setPlayerNames(List<Couple<Integer,String>> players) {
    for (Couple<Integer,String> couple : players) {
      gamePanel.addPlayer(couple.getFirst(),couple.getSecond());
    }
    messageLbl.setText("Score: 0");
  }

  /**
   * Repainting data on the screen.
   */
  public synchronized void flush() {
    gamePanel.repaint();
  }

  /**
   * Add a listener keys.
   */
  public void addKeyListener(KeyListener listener) {
    gameFrame.addKeyListener(listener);
  }

  public JFrame getFrame() {
    return gameFrame;
  }
}
