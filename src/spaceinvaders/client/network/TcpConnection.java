package spaceinvaders.client.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * TCP connection with the server.
 *
 * <p>Provides I/O through streams.
 */
public class TcpConnection extends NetworkConnection {
  private Socket socket;
  private BufferedReader reader;
  private PrintWriter writer;

  public TcpConnection(String serverAddr, Integer serverPort) {
    super(serverAddr,serverPort);
  }

  @Override
  public void connect() throws
      ServerNotFoundException,
      SocketIoException,
      ConnectionNotAllowedException,
      InvalidConnectionConfigurationException {
    try {
      socket = new Socket(getServerAddr(),getServerPort());
      reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      writer = new PrintWriter(socket.getOutputStream(),true);
    } catch (UnknownHostException exception) {
      throw new ServerNotFoundException();
    } catch (IOException exception) {
      throw new SocketIoException();
    } catch (SecurityException exception) {
      throw new ConnectionNotAllowedException();
    } catch (IllegalArgumentException exception) {
      throw new InvalidConnectionConfigurationException();
    }
  }

  @Override
  public void close() throws SocketIoException {
    try {
      if (socket != null && socket.isConnected() && !socket.isClosed()) {
        socket.close();
      }
    } catch (IOException exception) {
      throw new SocketIoException();
    }
  }

  @Override
  public String read() throws SocketIoException {
    String data = null;
    try {
      data = new String(reader.readLine());
    } catch (IOException exception) {
      throw new SocketIoException();
    } catch (NullPointerException exception) {
      data = null;
    }
    return data;
  }

  @Override
  public void send(String data) {
    writer.println(data);
  }
}
