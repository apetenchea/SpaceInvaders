package spaceinvaders.client.gui;

import static java.awt.BorderLayout.CENTER;
import static java.awt.BorderLayout.WEST;
import static java.util.logging.Level.SEVERE;
import static javax.swing.JFrame.DO_NOTHING_ON_CLOSE;
import static javax.swing.SwingConstants.LEFT;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import javax.swing.Box;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import spaceinvaders.Config;
import spaceinvaders.client.ClientConfig;
import spaceinvaders.client.ResourcesConfig;
import spaceinvaders.game.Entity;
import spaceinvaders.game.EntityEnum;
import spaceinvaders.game.GameConfig;
import spaceinvaders.utility.Couple;

/** Frame active during the game time. */
public class GameGraphics implements UiObject {
  private static final Logger LOGGER = Logger.getLogger(GameGraphics.class.getName());
  private final ResourcesConfig resources = ResourcesConfig.getInstance();
  private final JFrame frame = new JFrame("SpaceInvaders");
  private final GamePanel gamePanel = new GamePanel();
  private final JLabel messageLbl = new JLabel();
  private final JLabel[] scoreLbl = new JLabel[1 + ClientConfig.getInstance().getMaxPlayers()]; 
  private final JLabel controlsLbl = new JLabel();
  private final List<Couple<Integer,Integer>> score = new ArrayList<>();
  private final BufferedImage gameOverImg;
  private final BufferedImage victoryImg;
  private List<Couple<Integer,String>> playerNames = new ArrayList<>();

  /** Construct an empty game frame. */
  public GameGraphics() {
    final GameConfig config = GameConfig.getInstance();
    final int msgPanelWidth = 200;

    frame.setSize(msgPanelWidth + config.frame().getWidth(),config.frame().getHeight());
    frame.setResizable(false);
    frame.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
    /* Full screen. */
    frame.setUndecorated(true);

    final JPanel contentPane = new JPanel();
    contentPane.setBorder(new EmptyBorder(5,5,5,5));
    contentPane.setLayout(new BorderLayout(0,0));

    controlsLbl.setText("<html>Controls:"
        + "<br>ESC - exit"
        + "<br>SPACE - shoot"
        + "<br>LEFT/RIGHT - move</html>");

    final JPanel messagePanel = new JPanel();
    Box box = Box.createVerticalBox();
    messageLbl.setHorizontalAlignment(LEFT);
    box.add(messageLbl);
    box.add(controlsLbl);
    for (int index = 0; index < scoreLbl.length; ++index) {
      scoreLbl[index] = new JLabel();
      box.add(scoreLbl[index]);
    }
    scoreLbl[0].setText("Score");
    messagePanel.add(box);
    messagePanel.setPreferredSize(
        new Dimension(msgPanelWidth,config.frame().getHeight()));

    gamePanel.setPreferredSize(new Dimension(config.frame().getWidth(),config.frame().getHeight()));

    contentPane.add(messagePanel,WEST);
    contentPane.add(gamePanel,CENTER);

    frame.setContentPane(contentPane);

    try {
      gameOverImg = Config.getInstance().getImageResource(resources.getDefeatImage());
      victoryImg = Config.getInstance().getImageResource(resources.getVictoryImage());
    } catch (IOException ioException) {
      LOGGER.log(SEVERE,ioException.toString(),ioException);
      throw new AssertionError();
    }
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
    for (int index = 1; index < scoreLbl.length; ++index) {
      scoreLbl[index].setText("");
    }
    score.clear();
    playerNames.clear();
    gamePanel.init();
  }

  /** Repainting data on the screen. */
  public void flush() {
    gamePanel.repaint();
    gamePanel.revalidate();
  }

  /**
   * Match players ID's with names.
   *
   * @param couples a list of 2-tuples of the form {id, name}.
   *
   * @throws NullPointerException if argument is {@code null}.
   */
  public void setPlayerNames(List<Couple<Integer,String>> couples) {
    if (couples == null) {
      throw new NullPointerException();
    }
    playerNames = couples;
    int index = 1;
    for (Couple<Integer,String> couple : couples) {
      gamePanel.setPlayer(couple.getFirst(),couple.getSecond());
      score.add(new Couple<>(couple.getFirst(),0));
      scoreLbl[index++].setText(couple.getSecond() + ": 0");
    }
  }

  /**
   * Set the text of the message label.
   *
   * @throws NullPointerException if argument is {@code null}.
   */
  public void setMessage(String msg) {
    if (msg == null) {
      throw new NullPointerException();
    }
    messageLbl.setText(msg);
  }

  /** Display a game over image on the game panel. */
  public void showDefeatImage() {
    gamePanel.showImage(gameOverImg);
  }

  /** Display a victory image on the game panel. */
  public void showVictoryImage() {
    gamePanel.showImage(victoryImg);
  }

  /** Change score. */
  public void changeScore(int playerId, int value) {
    for (int index = 0; index < score.size(); ++index) {
      int id = score.get(index).getFirst();
      int scr = score.get(index).getSecond();
      if (id == playerId) {
        scr = Math.max(0,scr + value);
        score.set(index,new Couple<>(id,scr));
        scoreLbl[index + 1].setText(playerNames.get(index).getSecond() + ": " + scr);
      }
    }
  }

  /**
   * Set the contents of the game frame.
   *
   * @param content a list of active entities in the game.
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
