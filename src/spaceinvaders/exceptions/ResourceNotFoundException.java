package spaceinvaders.exceptions;

import java.io.IOException;

/**
 * Thrown when the connection times out.
 */
@SuppressWarnings("serial")
public class ResourceNotFoundException extends IOException {
  private static final String MESSAGE = "Resource not found!"; 

  public ResourceNotFoundException() {
    super(MESSAGE);
  }

  public ResourceNotFoundException(Throwable cause) {
    super(MESSAGE,cause);
  }
}
