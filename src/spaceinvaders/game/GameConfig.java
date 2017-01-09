package spaceinvaders.game;

import java.awt.Color;
import java.awt.Font;
import spaceinvaders.utility.Couple;

/**
 * Game configuration.
 *
 * <p>Specifies sizes of objects and resources.
 */
public class GameConfig {
  private static final String RESOURCES_FOLDER = "../resources/";

  private static GameConfig singleton;

  private String title;

  private Couple<Integer,Integer> gameFrameSize;

  private Color gamePanelForegroundColor;
  private Color gamePanelTextColor;
  private Font gamePanelTextFont;

  private Couple<Integer,Integer> playerSize;
  private Integer playerNameOffset;
  private String playerImage;

  private Couple<Integer,Integer> invaderSize;
  private String invaderImage;

  private Couple<Integer,Integer> bulletSize;
  private String invaderBulletImage;
  private String playerBulletImage;

  private Couple<Integer,Integer> shieldSize;
  private String shieldImage;

  private String destroyedEntityImage;

  private GameConfig() {
    title = "SpaceInvaders";

    gameFrameSize = new Couple<>(1280,760);

    gamePanelForegroundColor = Color.BLACK;
    gamePanelTextColor = Color.WHITE;
    gamePanelTextFont = new Font("Courier",Font.BOLD,15);

    playerSize = new Couple<>(64,64);
    playerNameOffset = 16;
    playerImage = RESOURCES_FOLDER + "spacecraft.png";

    invaderSize = new Couple<>(64,64);
    invaderImage = RESOURCES_FOLDER + "ufo.png";

    bulletSize = new Couple<>(64,64);
    invaderBulletImage = RESOURCES_FOLDER + "asteroid.png";
    playerBulletImage = RESOURCES_FOLDER + "bullet.png";

    shieldSize = new Couple<>(32,32);
    shieldImage = RESOURCES_FOLDER + "brickwall.png";
  }

  static public synchronized GameConfig getInstance() {
    if (singleton == null) {
      singleton = new GameConfig();
    }
    return singleton;
  }

  public String getTitle() {
    return title;
  }

  public int getGameFrameWidth() {
    return gameFrameSize.getFirst();
  }

  public int getGameFrameHeight() {
    return gameFrameSize.getSecond();
  }

  public Color getGamePanelForegroundColor() {
    return gamePanelForegroundColor;
  }

  public Color getGamePanelTextColor() {
    return gamePanelTextColor;
  }

  public Font getGamePanelTextFont() {
    return gamePanelTextFont;
  }

  public int getPlayerWidth() {
    return playerSize.getFirst();
  }

  public int getPlayerHeight() {
    return playerSize.getSecond();
  }

  public Couple<Integer,Integer> getPlayerSize() {
    return playerSize;
  }

  public int getPlayerNameOffset() {
    return playerNameOffset;
  }

  public String getPlayerImage() {
    return playerImage;
  }

  public int getInvaderWidth() {
    return invaderSize.getFirst();
  }

  public int getInvaderHeight() {
    return invaderSize.getSecond();
  }

  public Couple<Integer,Integer> getInvaderSize() {
    return invaderSize;
  }

  public String getInvaderImage() {
    return invaderImage;
  }

  public int getBulletWidth() {
    return bulletSize.getFirst();
  }

  public int getBulletHeight() {
    return bulletSize.getSecond();
  }

  public Couple<Integer,Integer> getBulletSize() {
    return bulletSize;
  }

  public String getInvaderBulletImage() {
    return invaderBulletImage;
  }

  public String getPlayerBulletImage() {
    return playerBulletImage;
  }

  public int getShieldWidth() {
    return shieldSize.getFirst();
  }

  public int getShieldHeight() {
    return shieldSize.getSecond();
  }

  public Couple<Integer,Integer> getShieldSize() {
    return shieldSize;
  }

  public String getShieldImage() {
    return shieldImage;
  }
}
