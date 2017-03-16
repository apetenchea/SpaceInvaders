package spaceinvaders.client;

import static java.util.logging.Level.SEVERE;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import spaceinvaders.Config;
import spaceinvaders.game.EntityEnum;

/** Used to retrive resources. */
public class ResourcesConfig {
  private static final transient Logger LOGGER = Logger.getLogger(ResourcesConfig.class.getName());
  private static transient ResourcesConfig singleton;

  private Map<EntityEnum,List<String>> avatarsMap;
  private String defeatImage;
  private String victoryImage;

  private ResourcesConfig() {}

  /** Singleton. */
  public static ResourcesConfig getInstance() {
    if (singleton == null) {
      try {
        singleton = readConfig();
      } catch (IOException ioExcepiton) {
        LOGGER.log(SEVERE,ioExcepiton.toString(),ioExcepiton);
      }
    }
    return singleton;
  }

  /**
   * Get the configuration file from a json.
   *
   * @throws IOException if an error occurs while reading the configuration file.
   */
  private static ResourcesConfig readConfig() throws IOException {
    final Config config = Config.getInstance();
    return config.getJsonResource(config.getResourcesConfigFile(),ResourcesConfig.class);
  }

  /**
   * @param type the type of entity.
   * 
   * @return list of paths to all avatars.
   * 
   * @throws NullPointerException if the argument is {@code null} or the entity type does not
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
}
