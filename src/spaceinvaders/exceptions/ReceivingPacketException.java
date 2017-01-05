package spaceinvaders.exceptions;

import java.io.IOException;

/**
 * Thrown if a packet failed upon arrival.
 */
@SuppressWarnings("serial")
public class ReceivingPacketException extends IOException {
  public ReceivingPacketException(Throwable cause) {
    super("An error occured while receiving an UDP packet!",cause);
  }
}
