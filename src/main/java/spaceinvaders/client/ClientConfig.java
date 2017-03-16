package spaceinvaders.client;

import static java.util.logging.Level.SEVERE;

import java.io.IOException;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import spaceinvaders.Config;
import spaceinvaders.exceptions.IllegalPortNumberException;
import spaceinvaders.exceptions.InvalidServerAddressException;
import spaceinvaders.exceptions.InvalidUserNameException;

/** Used to hold the configuration of the client. */
public class ClientConfig {
  private static final transient Logger LOGGER = Logger.getLogger(ClientConfig.class.getName());
  private static transient ClientConfig singleton;

  private Integer maxUserNameLength;
  private Integer maxPlayersPerTeam;
  private Integer id;
  private Integer teamSize;
  private String serverAddr;
  private Integer serverPort;
  private String userName;
  private Integer udpIncomingPort;

  private ClientConfig() {}

  /** Singleton. */
  public static synchronized ClientConfig getInstance() {
    if (singleton == null) {
      try {
        singleton = readConfig();
      } catch (IOException ioException) {
        LOGGER.log(SEVERE,ioException.toString(),ioException);
      }
    }
    return singleton;
  }

  /**
   * Reads the configuration form a json.
   *
   * @throws IOException if an error occurs while reading the configuration file.
   */
  private static ClientConfig readConfig() throws IOException {
    final Config config = Config.getInstance();
    return config.getJsonResource(config.getClientConfigFile(),ClientConfig.class);
  }

  public int getMaxUserNameLength() {
    return maxUserNameLength;
  }

  public int getMaxPlayers() {
    return maxPlayersPerTeam;
  }

  /** Check if the server address is valid. */
  public boolean isAddrValid() {
    final Pattern validIpAddressRegex = Pattern.compile(
        "^(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.){3}"
        + "([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])$");
    final Pattern validHostNameRegex = Pattern.compile(
        "^(([a-zA-Z0-9]|[a-zA-Z0-9][a-zA-Z0-9\\-]*[a-zA-Z0-9])\\.)*"
        + "([A-Za-z0-9]|[A-Za-z0-9][A-Za-z0-9\\-]*[A-Za-z0-9])$");
    return validIpAddressRegex.matcher(serverAddr).matches()
      || validHostNameRegex.matcher(serverAddr).matches();
  }

  /**
   * Check if the port number is valid.
   */
  public boolean isPortValid() {
    return 0 <= serverPort && serverPort <= 65535;
  }

  /**
   * Check if the user name is valid.
   */
  public boolean isUserNameValid() {
    final Pattern validUserName = Pattern.compile("^([a-z]|[A-Z])([a-z]|[A-z]|\\d)+$");
    return userName.length() <= maxUserNameLength && validUserName.matcher(userName).matches();
  }

  /** Check the data integrity. */
  public void verify() throws InvalidServerAddressException, IllegalPortNumberException,
         InvalidUserNameException {
    if (!isPortValid()) {
      throw new IllegalPortNumberException();
    }
    if (!isAddrValid()) {
      throw new InvalidServerAddressException();
    }
    if (!isUserNameValid()) {
      throw new InvalidUserNameException();
    }
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public int getTeamSize() {
    return teamSize;
  }

  public void setTeamSize(int teamSize) {
    this.teamSize = teamSize;
  }

  public String getServerAddr() {
    return serverAddr;
  }

  public void setServerAddr(String serverAddr) {
    this.serverAddr = serverAddr;
  }

  public int getServerPort() {
    return serverPort;
  }

  public int getUdpIncomingPort() {
    return udpIncomingPort;
  }

  public void setUdpIncomingPort(int udpIncomingPort) {
    this.udpIncomingPort = udpIncomingPort;
  }

  public void setServerPort(int serverPort) {
    this.serverPort = serverPort;
  }

  public String getUserName() {
    return userName;
  }

  public void setUserName(String userName) {
    this.userName = userName ;
  }
}
