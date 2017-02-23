package spaceinvaders.client;

import static spaceinvaders.game.EntityEnum.INVADER;
import static spaceinvaders.game.EntityEnum.INVADER_BULLET;
import static spaceinvaders.game.EntityEnum.PLAYER;
import static spaceinvaders.game.EntityEnum.PLAYER_BULLET;
import static spaceinvaders.game.EntityEnum.SHIELD;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import spaceinvaders.game.EntityEnum;

/** Used to retrive resources. */
public class ResourcesConfig {
  //private static final String RESOURCES_CONFIG_FILE = "../resources/";
  private static final String RESOURCES_FOLDER = "../resources/";
  private static ResourcesConfig singleton;

  private final Map<EntityEnum,List<String>> avatarsMap;
  private final String defeatImage;
  private final String victoryImage;

  private ResourcesConfig() {
    avatarsMap = new HashMap<>();
    avatarsMap.put(PLAYER,new ArrayList<>(Arrays.asList(
          RESOURCES_FOLDER + "spacecraft1.png",
          RESOURCES_FOLDER + "spacecraft2.png",
          RESOURCES_FOLDER + "spacecraft3.png")));
    avatarsMap.put(INVADER,new ArrayList<>(Arrays.asList(
          RESOURCES_FOLDER + "ufo.png")));
    avatarsMap.put(INVADER_BULLET,new ArrayList<>(Arrays.asList(
          RESOURCES_FOLDER + "asteroid.png")));
    avatarsMap.put(PLAYER_BULLET,new ArrayList<>(Arrays.asList(
          RESOURCES_FOLDER + "bullet.png")));
    avatarsMap.put(SHIELD,new ArrayList<>(Arrays.asList(
          RESOURCES_FOLDER + "brickwall.png")));
    defeatImage = RESOURCES_FOLDER + "poison.png";
    victoryImage = RESOURCES_FOLDER + "stars.png";
  }

  /** Singleton. */
  public static ResourcesConfig getInstance() {
    if (singleton == null) {
      singleton = new ResourcesConfig();
    }
    return singleton;
  }

  /**
   * @param type - the type of entity.
   * 
   * @return list of paths to all avatars.
   * 
   * @throws NullPointerException - if the argument is {@code null} or the entity type does not
   *     have any avatars.
   */
  public List<String> getAvatars(EntityEnum type) {
    if (type == null) {
      throw new NullPointerException();
    }
    List<String> avatars = avatarsMap.get(type);
    if (avatars == null) {
      throw new NullPointerException();
    }
    return avatars;
  }

  public String getDefeatImage() {
    return defeatImage;
  }

  public String getVictoryImage() {
    return victoryImage;
  }

  /*
  private void readConfig() {
  }

  private class Config {
    public List<String> playerAvatars;
    public List<String> invaderAvatars;
    public List<String> invaderBulletAvatars;
    public List<String> playerBulletAvatars;
    public List<String> shieldAvatars;
  }
  */
}
