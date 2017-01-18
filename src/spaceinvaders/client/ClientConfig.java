package spaceinvaders.client;

import java.net.SocketAddress;
import java.util.regex.Pattern;
import spaceinvaders.exceptions.IllegalPortNumberException;
import spaceinvaders.exceptions.InvalidServerAddressException;
import spaceinvaders.exceptions.InvalidUserNameException;

/** Used to maintain the configuration of the client. */
public class ClientConfig {
  private static ClientConfig singleton;

  private Integer id;
  private Integer teamSize = 1;
  private String serverAddr = "localhost";
  private Integer serverPort = 5412;
  private String userName = "default";
  private SocketAddress udpIncomingAddr;

  /**
   * Get a ClientConfig instance.
   */
  public static synchronized ClientConfig getInstance() {
    if (singleton == null) {
      singleton = new ClientConfig();
    }
    return singleton;
  }

  /**
   * Check if the server address is valid.
   */
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
    return userName.length() <= 10 && validUserName.matcher(userName).matches();
  }

  /**
   * Verify the data integrity.
   */
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

  public void setId(int id) {
    this.id = id;
  }

  public int getId() {
    return id;
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

  public SocketAddress getUdpIncomingAddr() {
    return udpIncomingAddr;
  }

  public void setUdpIncomingAddr(SocketAddress udpIncomingAddr) {
    this.udpIncomingAddr = udpIncomingAddr;
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
