package spaceinvaders.server.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.concurrent.Callable;
import spaceinvaders.exceptions.ReceivingPacketException;
import spaceinvaders.exceptions.SocketOpeningException;
import spaceinvaders.utility.ServiceState;

/**
 * Receives UDP packets.
 */
public class UdpListener implements Callable<Void> {
  private static int PACKET_SIZE = 16;

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
  public Void call() throws ReceivingPacketException {
    while (state.get()) {
      packet = new DatagramPacket(new byte[PACKET_SIZE],PACKET_SIZE);
      try {
        socket.receive(packet);
      } catch (IOException exception) {
        throw new ReceivingPacketException(exception);
      }
      String data = new String(packet.getData());
      System.err.println(data);
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
