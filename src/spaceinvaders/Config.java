package spaceinvaders;

public class Config {
  private static final String CONFIG_PATH = "../config/";
  private static Config singleton;


  private Config() {}

  public static Config getInstance() {
    if (singleton == null) {
      singleton = new Config();
    }
    return singleton;
  }

  public String getGameConfigFile() {
    return CONFIG_PATH + "GameConfig.json";
  }

  public String getResourcesConfigFile() {
    return CONFIG_PATH + "ResourcesConfig.json";
  }

  public String getClientConfigFile() {
    return CONFIG_PATH + "ClientConfig.json";
  }
}
