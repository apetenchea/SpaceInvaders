package spaceinvaders;

import static java.util.logging.Level.SEVERE;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 * Main configuraion manager.
 *
 * <p>Contains application global configuration. Is also used to retrive other configuration files.
 */
public class Config {
  private static final transient Logger LOGGER = Logger.getLogger(Config.class.getName());
  private static final transient String CONFIG_PATH = "/config/";

  /**
   * If set to {@code true}, resources are taken from inside the jar archive, otherwise, they are
   * taken as regular files.
   */
  private static final transient Boolean JAR_FILE = true;

  private static transient Config singleton;

  /**
   * Game is played in LAN.
   *
   * <p>If set to {@code false}, only the TCP protocol is used for communication. Otherwise, UDP is
   * used as well, thus increasing the speed.
   */
  private Boolean lanGame;

  /**
   * Get the single instance of this class.
   *
   * <p>If an instance does not exist, one is created automatically.
   */
  public static Config getInstance() {
    if (singleton == null) {
      try {
        Config cfg = new Config();
        singleton = cfg.getJsonResource(CONFIG_PATH + "app.json",cfg.getClass());
      } catch (IOException ioException) {
        LOGGER.log(SEVERE,ioException.toString(),ioException);
      }
    }
    return singleton;
  }

  /**
   * @return true if the game is played in LAN, false otherwise.
   */
  public boolean isLanGame() {
    return lanGame;
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

  /**
   * Reads a json resource file and returns the object.
   *
   * @param path path to the resource file.
   * @param classOfT the type of object to be converted from json.
   *
   * @return an object of type T, converted from json.
   *
   * @throws IOException if an I/O exception occurs while reading the file.
   * @throws FileNotFoundException if the file cannot be found.
   * @throws OutOfMemoryError if the file is too large.
   * @throws JsonSyntaxException if the json is not a valid representation of {@code classOfT}.
   * @throws InvalidPathException if the path to the file is invalid.
   * @throws SecurityException if the file cannot be accessed.
   * @throws NullPointerException if an argument is {@code null}.
   */
  public <T> T getJsonResource(String path, Class<T> classOfT)
      throws FileNotFoundException, IOException {
    if (path == null || classOfT == null) {
      throw new NullPointerException();
    }
    String json = null;
    if (JAR_FILE) {
      InputStream input = getClass().getResourceAsStream(path);
      if (input == null) {
        throw new FileNotFoundException();
      }
      BufferedReader reader = new BufferedReader(new InputStreamReader(input));
      final StringBuffer buf = new StringBuffer();
      int piece = -1;
      while ((piece = reader.read()) != -1) {
        buf.append((char) piece);
      }
      reader.close();
      json = buf.toString();
    } else {
      json = new String(Files.readAllBytes(Paths.get(path)));
    }
    final Gson gson = new Gson();
    T obj = gson.fromJson(json,classOfT);
    return obj;
  }

  /**
   * Get an image resource.
   *
   * @param path path to the resource.
   *
   * @return the image object.
   *
   * @throws IOException if an error occurs during reading.
   * @throws FileNotFoundException if the image cannot be found.
   * @throws NullPointerException if argument is {@code null}.
   */
  public BufferedImage getImageResource(String path) throws FileNotFoundException, IOException  {
    if (JAR_FILE) {
      InputStream input = getClass().getResourceAsStream(path);
      if (input == null) {
        throw new FileNotFoundException();
      }
      return ImageIO.read(input);
    }
    return ImageIO.read(new File(path));
  }
}
