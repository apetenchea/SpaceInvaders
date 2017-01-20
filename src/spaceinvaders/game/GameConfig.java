package spaceinvaders.game;

import com.google.gson.Gson;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import spaceinvaders.game.Entity;
import spaceinvaders.game.EntityEnum;
import spaceinvaders.utility.Couple;

/** Configuration for game entities. */
public class GameConfig {
  private static transient GameConfig singleton;

  private transient Map<EntityEnum,EntityConfig> entityMap = new HashMap<>();
  private Integer invaderRows = 3;
  private Integer invaderCols = 7;
  private Integer invadersShootingRate = 1950;
  private Integer shieldsPerPlayer = 3;
  private FrameConfig frame;
  private Speed speed;

  private GameConfig() {
    frame = new FrameConfig();
    speed = new Speed();
    entityMap.put(EntityEnum.INVADER,new EntityConfig(EntityEnum.INVADER,64,64));
    entityMap.put(EntityEnum.PLAYER,new EntityConfig(EntityEnum.PLAYER,64,64));
    entityMap.put(EntityEnum.SHIELD,new EntityConfig(EntityEnum.SHIELD,32,32));
    entityMap.put(EntityEnum.PLAYER_BULLET,new EntityConfig(EntityEnum.PLAYER_BULLET,24,24));
    entityMap.put(EntityEnum.INVADER_BULLET,new EntityConfig(EntityEnum.INVADER_BULLET,24,24));
  }

  public static GameConfig getInstance() {
    if (singleton == null) {
      try {
        singleton = new GameConfig();
      } catch (Exception e) {
        System.err.println(e);
      }
    }
    return singleton;
  }

  /**
   * TODO json errors
   * Configure world.
   *
   * @throws IOException - if an error occurs while reading the configuration file.
   * @throws OutOfMemoryError - if the configuration file is too large.
   * @throws InvalidPathException - if the configuration file cannot be found.
   * @throws SecurityException - if an operation is not allowed.
   */
  private static GameConfig readConfig() throws IOException {
    String json = new String(Files.readAllBytes(
          Paths.get("/home/alex/work/eclipse/SpaceInvaders/WorldConfig.json")));
    Gson gson = new Gson();
    return gson.fromJson(json,GameConfig.class);
  }

  public FrameConfig frame() {
    return frame;
  }

  public Speed speed() {
    return speed();
  }

  public int getInvaderRows() {
    return invaderRows;
  }

  public int getInvaderCols() {
    return invaderCols;
  }

  public int getInvadersShootingRate() {
    return invadersShootingRate;
  }

  public int getShieldsPerPlayer() {
    return shieldsPerPlayer;
  }

  public EntityConfig invader() {
    return getFromMap(EntityEnum.INVADER);
  }

  public EntityConfig player() {
    return getFromMap(EntityEnum.PLAYER);
  }

  public EntityConfig shield() {
    return getFromMap(EntityEnum.SHIELD);
  }

  public EntityConfig invaderBullet() {
    return getFromMap(EntityEnum.INVADER_BULLET);
  }

  public EntityConfig playerBullet() {
    return getFromMap(EntityEnum.PLAYER_BULLET);
  }

  private EntityConfig getFromMap(EntityEnum type) {
    EntityConfig conf = entityMap.get(type);
    if (conf == null) {
      throw new AssertionError();
    }
    return conf;
  }

  public class FrameConfig {
    Integer width = 1280;
    Integer height = 760;

    public int getWidth() {
      return width;
    }

    public int getHeight() {
      return height;
    }
  }

  public class EntityConfig {
    private EntityEnum type;
    private Integer width;
    private Integer height;

    public EntityConfig(EntityEnum type, int width, int height) {
      this.type = type;
      this.width = width;
      this.height = height;
    }

    public int getWidth() {
      return width;
    }

    public int getHeight() {
      return height;
    }
  }

  public class SpeedConfig {
    private Integer distance;
    private Integer rate;

    public SpeedConfig(int d, int r) {
      distance = d;
      rate = r;
    }

    public int getDistance() {
      return distance;
    }

    public int getRate() {
      return rate;
    }
  }

  public class Speed {
    private SpeedConfig invader;
    private SpeedConfig player;
    private SpeedConfig bullet;

    public Speed() {
      invader = new SpeedConfig(32,1000);
      player = new SpeedConfig(32,0);
      bullet = new SpeedConfig(4,300);
    }

    public SpeedConfig invader() {
      return invader;
    }

    private SpeedConfig player() {
      return player;
    }

    public SpeedConfig bullet() {
      return bullet;
    }
  }
}
