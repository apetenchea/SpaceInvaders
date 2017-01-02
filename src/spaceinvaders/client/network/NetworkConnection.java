package spaceinvaders.client.network;

/**
 * Network connection used to communicate with the game server.
 *
 * @see spaceinvaders.server.Server
 */
public abstract class NetworkConnection {
  private String serverAddr;
  private Integer serverPort;

  /**
   * Initialize connection parameters.
   */
  public NetworkConnection(String serverAddr, Integer serverPort) {
    this.serverAddr = serverAddr;
    this.serverPort = serverPort;
  }

  public String getServerAddr() {
    return serverAddr;
  }

  public Integer getServerPort() {
    return serverPort;
  }

  /**
   * Establish the connection.
   */
  public abstract void connect() throws
      ServerNotFoundException,
      SocketIoException,
      ConnectionNotAllowedException,
      InvalidConnectionConfigurationException;

  /**
   * Close connection.
   */
  public abstract void close() throws SocketIoException;
 
  /**
   * Read data.
   */
  public abstract String read() throws SocketIoException;

  /**
   * Send data over the network.
   *
   * @param data the string to be sent.
   */
  public abstract void send(String data) throws SocketIoException;
}
