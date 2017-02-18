package spaceinvaders.client.gui;

import static java.awt.BorderLayout.CENTER;
import static java.awt.BorderLayout.NORTH;
import static javax.swing.JFrame.DO_NOTHING_ON_CLOSE;
import static javax.swing.SwingConstants.LEFT;

import java.awt.BorderLayout;
import java.awt.event.KeyListener;
import java.util.List;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import spaceinvaders.game.Entity;
import spaceinvaders.game.EntityEnum;
import spaceinvaders.game.GameConfig;
import spaceinvaders.utility.Couple;

/** Displays the game. */
public class GameGraphics implements UiObject {
  private static final Logger LOGGER = Logger.getLogger(GameGraphics.class.getName());

  // TODO try to eliminate the need of this.
  private static final int FRAME_HEIGHT_COMPENSATION = 64;

  private final JFrame frame = new JFrame("SpaceInvaders");
  private final GamePanel gamePanel = new GamePanel();
  private final JLabel messageLbl = new JLabel();
  private Integer score;

  /** Construct an empty game frame. */
  public GameGraphics() {
    final GameConfig config = GameConfig.getInstance();

    frame.setResizable(false);
    frame.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
    frame.setSize(config.frame().getWidth(),config.frame().getHeight() + FRAME_HEIGHT_COMPENSATION);

    final JPanel contentPane = new JPanel();
    contentPane.setBorder(new EmptyBorder(5,5,5,5));
    contentPane.setLayout(new BorderLayout(0,0));
    frame.setContentPane(contentPane);

    final JPanel messagePanel = new JPanel();
    messageLbl.setHorizontalAlignment(LEFT);
    messagePanel.add(messageLbl);

    contentPane.add(messagePanel,NORTH);
    contentPane.add(gamePanel,CENTER);
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

  /** Add a listener for key bindings. */
  public void addKeyListener(KeyListener listener) {
    frame.addKeyListener(listener);
  }

  /** Initialize game. */
  public void init() {
    score = 0;
    gamePanel.init();
  }

  /** Repainting data on the screen. */
  public void flush() {
    gamePanel.repaint();
  }

  /**
   * Match players ID's with names.
   *
   * @param couples - a list of 2-tuples of the form {id,name}.
   *
   * @throws NullPointerException - if argument is {@code null}.
   */
  public void setPlayerNames(List<Couple<Integer,String>> couples) {
    if (couples == null) {
      throw new NullPointerException();
    }
    for (Couple<Integer,String> couple : couples) {
      gamePanel.setPlayer(couple.getFirst(),couple.getSecond());
    }
  }

  /**
   * Set the text of the message label.
   *
   * @throws NullPointerException - if argument is {@code null}.
   */
  public void setMessage(String msg) {
    if (msg == null) {
      throw new NullPointerException();
    }
    messageLbl.setText(msg);
  }

  /** Increment score. */
  public void incrementScore() {
    ++score;
    setMessage("Score: " + Integer.toString(score));
  }

  /**
   * Set the contents of the game frame.
   *
   * @param content - a list of active entities in the game.
   *
   * @throws NullPointerException - if argument is {@code null}.
   */
  public void setFrameContent(List<Entity> content) {
    if (content == null) {
      throw new NullPointerException();
    }
    gamePanel.refreshEntities(content);
  }

  /** Move an entity on the screen. */
  public void moveEntity(int entityId, int newX, int newY) {
    gamePanel.relocateEntity(entityId,newX,newY);
  }

  /** Spawn an entity on the screen. */
  public void spawnEntity(int id, EntityEnum type, int posX, int posY) {
    gamePanel.spawnEntity(id,type,posX,posY);
  }

  /** Do not desplay an entity any more. */
  public void wipeOutEntity(int id) {
    gamePanel.wipeOutEntity(id);
  }

  /**
   * Move all entities of a given {@code type} on the screen.
   */
  public void translateGroup(EntityEnum type, int offsetX, int offsetY) {
    gamePanel.translateGroup(type,offsetX,offsetY);
  }

  public JFrame getFrame() {
    return frame;
  }
}
