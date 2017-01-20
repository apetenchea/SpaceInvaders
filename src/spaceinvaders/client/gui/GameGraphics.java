package spaceinvaders.client.gui;

import java.util.List;
import java.awt.event.KeyListener;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import spaceinvaders.game.GameConfigOld;
import spaceinvaders.game.Entity;
import spaceinvaders.utility.Couple;

/** Display the game. */
public class GameGraphics implements UiObject {
  private static final Logger LOGGER = Logger.getLogger(GameGraphics.class.getName());

  private final JFrame frame;
  private final GamePanel gamePanel = new GamePanel();
  private JLabel messageLbl = new JLabel();

  /** Construct an empty game frame. */
  public GameGraphics() {
    GameConfigOld config = GameConfigOld.getInstance();
    frame = new JFrame();
    frame.setResizable(false);
    frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    frame.setSize(config.getGameFrameWidth(),config.getGameFrameHeight());

    JPanel contentPane = new JPanel();
    contentPane.setBorder(new EmptyBorder(5,5,5,5));
    contentPane.setLayout(new BorderLayout(0,0));
    frame.setContentPane(contentPane);

		JPanel messagePanel = new JPanel();
		messageLbl.setHorizontalAlignment(SwingConstants.LEFT);
		messagePanel.add(messageLbl);

		contentPane.add(messagePanel,BorderLayout.NORTH);
		contentPane.add(gamePanel,BorderLayout.CENTER);
		gamePanel.setForeground(config.getGamePanelForegroundColor());
  }

  @Override
  public void destroy() {
    frame.dispose();
  }

  @Override
  public void hide() {
    frame.setVisible(false);
  }

  @Override
  public void show() {
    messageLbl.setText("Waiting for server...");
    frame.setVisible(true);
  }

  public void setPlayerNames(List<Couple<Integer,String>> players) {
    for (Couple<Integer,String> couple : players) {
      gamePanel.addPlayer(couple.getFirst(),couple.getSecond());
    }
  }

  public void setMessage(String msg) {
    messageLbl.setText(msg);
  }

  public void setFrameContent(List<Entity> content) {
    gamePanel.refreshEntities(content);
  }

  /** Repainting data on the screen. */
  public void flush() {
    gamePanel.repaint();
  }

  /** Add a listener for key bindings. */
  public void addKeyListener(KeyListener listener) {
    frame.addKeyListener(listener);
  }

  public JFrame getFrame() {
    return frame;
  }
}
