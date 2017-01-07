package spaceinvaders.server.network;

import java.io.IOException;
import java.util.Observable;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.concurrent.Callable;
import spaceinvaders.exceptions.SocketOpeningException;
import spaceinvaders.utility.ServiceState;

/**
 * Receives UDP packets.
 */
public class UdpListener extends Observable implements Callable<Void> {
  private static final Logger LOGGER = Logger.getLogger(UdpListener.class.getName());
  private static final int PACKET_SIZE = 16;

  private DatagramSocket socket;
  private DatagramPacket packet;

  private ServiceState state;

  /**
   * Start an UDP listener at the specified port.
   */
  public UdpListener(int port) throws SocketOpeningException {
    try {
      socket = new DatagramSocket(port);
    } catch (SocketException exception) {
      throw new SocketOpeningException(exception);
    }
    state = new ServiceState(true);
  }

  @Override 
  public Void call() {
    while (state.get()) {
      packet = new DatagramPacket(new byte[PACKET_SIZE],PACKET_SIZE);
      try {
        socket.receive(packet);
      } catch (IOException exception) {
        if (state.get()) {
          LOGGER.log(Level.SEVERE,exception.getMessage(),exception);
        }
      }
      String data = new String(packet.getData()).trim();
      if (state.get() && data.length() > 0) {
        setChanged();
        notifyObservers(data);
      }
    }
    return null;
  }

  /**
   * Close listener.
   */
  public void close() {
    state.set(false);
    socket.close();
  }
}
