package spaceinvaders;

/**
 * Main configuraion.
 *
 * <p>Used to retrive the path to configuration files
 */
public class Config {
  private static final String CONFIG_PATH = "../config/";
  private static Config singleton;

  private Config() {}

  /**
   * Get the single instance of this class.
   *
   * <p>If an instance does not exist, one is created automatically.
   */
  public static Config getInstance() {
    if (singleton == null) {
      singleton = new Config();
    }
    return singleton;
  }

  public String getGameConfigFile() {
    return CONFIG_PATH + "game.json";
  }

  public String getResourcesConfigFile() {
    return CONFIG_PATH + "resources.json";
  }

  public String getClientConfigFile() {
    return CONFIG_PATH + "client.json";
  }
}
