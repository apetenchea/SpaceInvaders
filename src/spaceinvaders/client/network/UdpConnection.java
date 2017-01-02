package spaceinvaders.client.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * UDP connection with the server.
 *
 * <p>Used for ping-pong signal.
 */
public class UdpConnection extends NetworkConnection {
  private static final int BUFFER_SIZE = 16;
  private InetAddress serverIpAddress;
  private DatagramSocket socket;

  public UdpConnection(String serverAddr, Integer serverPort) {
    super(serverAddr,serverPort);
  }

  @Override
  public void connect() throws
      ServerNotFoundException,
      SocketIoException,
      ConnectionNotAllowedException,
      InvalidConnectionConfigurationException {
    try {
      InetAddress serverIpAddress = InetAddress.getByName(getServerAddr());
      socket = new DatagramSocket();
      socket.connect(serverIpAddress,getServerPort());
    } catch (UnknownHostException exception) {
      throw new ServerNotFoundException();
    } catch (SocketException exception) {
      throw new SocketIoException();
    } catch (SecurityException exception) {
      throw new ConnectionNotAllowedException();
    } catch (IllegalArgumentException exception) {
      throw new InvalidConnectionConfigurationException();
    }
  }

  @Override
  public void close() {
    if (socket != null && socket.isConnected() && !socket.isClosed()) {
      socket.close();
    }
  }

  @Override
  public String read() throws SocketIoException {
    String data = null;
    DatagramPacket inPacket = new DatagramPacket(new byte[BUFFER_SIZE],BUFFER_SIZE);
    try {
      socket.receive(inPacket);
      data = new String(inPacket.getData());
    } catch (IOException exception) {
      throw new SocketIoException();
    }
    return data;
  }

  @Override
  public void send(String data) throws SocketIoException {
    DatagramPacket outPacket = new DatagramPacket(data.getBytes(),data.length(),
        serverIpAddress,getServerPort());
    try {
      socket.send(outPacket);
    } catch (IOException exception) {
      throw new SocketIoException();
    }
  }
}
