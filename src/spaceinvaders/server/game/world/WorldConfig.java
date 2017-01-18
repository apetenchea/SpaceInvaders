package spaceinvaders.server.game.world;

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
public class WorldConfig {
  private static transient WorldConfig singleton;

  private transient Map<EntityEnum,EntityConfig> entityMap = new HashMap<>();
  private FrameConfig frame;
  private Speed speed;

  private List<EntityConfig> entities = new ArrayList<>();

  private WorldConfig() {
    frame = new FrameConfig();
    speed = new Speed();
    entities.add(new EntityConfig(EntityEnum.INVADER,
  }

  public static WorldConfig getInstance() throws IOException {
    if (singleton == null) {
      try {
        singleton = new WorldConfig();
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
  private static WorldConfig readConfig() throws IOException {
    String json = new String(Files.readAllBytes(
          Paths.get("/home/alex/work/eclipse/SpaceInvaders/WorldConfig.json")));
    Gson gson = new Gson();
    return gson.fromJson(json,WorldConfig.class);
  }

  public FrameConfig frame() {
    return frame;
  }

  public Speed speed() {
    return speed();
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

  private class FrameConfig {
    private Couple<Integer,Integer> size;

    public FrameConfig() {
      size = new Couple<Integer,Integer>(1000,1000);
    }

    public int width() {
      return size.getFirst();
    }

    public int height() {
      return size.getSecond();
    }
  }

  private class EntityConfig {
    private EntityEnum type;
    private Couple<Integer,Integer> size;

    public EntityConfig(EntityEnum type, int width, int height) {
      this.type = type;
      size = new Couple<Integer,Integer>(width,height);
    }

    public int width() {
      return size.getFirst();
    }

    public int height() {
      return size.getSecond();
    }
  }

  private class SpeedConfig {
    private Couple<Integer,Integer> speed;

    public SpeedConfig(int d, int r) {
      speed = new Couple<Integer,Integer>(d,r);
    }

    public int distance() {
      return speed.getFirst();
    }

    public int rate() {
      return speed.getSecond();
    }
  }

  private class Speed {
    private SpeedConfig invader;
    private SpeedConfig player;
    private SpeedConfig bullet;

    public Speed() {
      invader = new SpeedConfig(10,1000);
      player = new SpeedConfig(10,0);
      bullet = new SpeedConfig(10,300);
    }

    public SpeedConfig invader() {
      return invader;
    }

    private SpeedConfig player() {
      return player;
    }

    private SpeedConfig bullet() {
      return bullet;
    }
  }
}
