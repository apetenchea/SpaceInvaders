package spaceinvaders.exceptions;

import java.io.IOException;

/**
 * Thrown if a packet failed upon arrival.
 */
@SuppressWarnings("serial")
public class ReceivingPacketException extends IOException {
  private static final String MESSAGE = "An error occured while receiving an UDP packet!";

  public ReceivingPacketException() {
    super(MESSAGE);
  }

  public ReceivingPacketException(Throwable cause) {
    super(MESSAGE,cause);
  }
}
