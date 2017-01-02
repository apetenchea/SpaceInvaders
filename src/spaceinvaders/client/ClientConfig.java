package spaceinvaders.client;

import java.util.regex.Pattern;

/**
 * Client's configuration.
 */
public class ClientConfig {
  private Integer id;
  private Integer noOfPlayers;
  private String serverAddr;
  private Integer serverPort;
  private String userName;

  /**
   * Default configuration.
   */
  public ClientConfig() {
    id = 0;
    serverAddr = "localhost";
    serverPort = 5412;
    userName = "default";
    noOfPlayers = 1;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj != null && obj instanceof ClientConfig) {
      ClientConfig other = (ClientConfig)obj;
      return other.id.equals(id)
        && other.serverAddr.equals(serverAddr)
        && other.serverPort.equals(serverPort)
        && other.userName.equals(userName)
        && other.noOfPlayers.equals(noOfPlayers);
    }
    return false;
  }

  @Override
  public int hashCode() {
    final int prime = 71;
    int hash = id.hashCode();
    hash = (prime * hash) ^ serverAddr.hashCode();
    hash = (prime * hash) ^ serverPort.hashCode();
    hash = (prime * hash) ^ userName.hashCode();
    hash = (prime * hash) ^ noOfPlayers.hashCode();
    return hash;
  }

  /**
   * Check if the server address is valid.
   */
  public Boolean isAddrValid() {
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
  public Boolean isPortValid() {
    return 0 <= serverPort && serverPort <= 65535;
  }

  /**
   * Check if the user name is valid.
   */
  public Boolean isUserNameValid() {
    final Pattern validUserName = Pattern.compile("^([a-z]|[A-Z])([a-z]|[A-z]|\\d)+$");
    return userName.length() <= 10 && validUserName.matcher(userName).matches();
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer newId) {
    id = newId;
  }

  public Integer getNoOfPlayers() {
    return noOfPlayers;
  }

  public void setNoOfPlayers(Integer newNoOfPlayers) {
    noOfPlayers = newNoOfPlayers;
  }

  public String getServerAddr() {
    return serverAddr;
  }

  public void setServerAddr(String newServerAddr) {
    serverAddr = newServerAddr;
  }

  public Integer getServerPort() {
    return serverPort;
  }

  public void setServerPort(Integer newServerPort) {
    serverPort = newServerPort;
  }

  public String getUserName() {
    return userName;
  }

  public void setUserName(String newUserName) {
    userName = newUserName;
  }
}
