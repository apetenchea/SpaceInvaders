package spaceinvaders.client;

import java.util.regex.Pattern;
import spaceinvaders.exceptions.IllegalPortNumberException;
import spaceinvaders.exceptions.InvalidServerAddressException;
import spaceinvaders.exceptions.InvalidUserNameException;

/** Used to maintain the configuration of the client. */
public class ClientConfig {
  private static ClientConfig singleton;
  private static int MAX_UNAME_LENGTH = 10;
  private static int MAX_PLAYERS = 3;

  private Integer id;
  private Integer teamSize;
  private String serverAddr;
  private Integer serverPort;
  private String userName;
  private Integer udpIncomingPort;

  private ClientConfig() {
    teamSize = 1;
    serverAddr = "localhost";
    serverPort = 5412;
    userName = "default";
  }

  /** Get a ClientConfig instance. */
  public static synchronized ClientConfig getInstance() {
    if (singleton == null) {
      singleton = new ClientConfig();
    }
    return singleton;
  }

  public int getMaxUserNameLength() {
    return MAX_UNAME_LENGTH;
  }

  public int getMaxPlayers() {
    return MAX_PLAYERS;
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
    return userName.length() <= MAX_UNAME_LENGTH && validUserName.matcher(userName).matches();
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
