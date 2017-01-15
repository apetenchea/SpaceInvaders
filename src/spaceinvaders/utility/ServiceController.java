package spaceinvaders.utility;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import spaceinvaders.exceptions.InterruptedServiceException;

/** Provides interaction with a service. */
public abstract class ServiceController implements Service<Void> {
  private final BufferedReader inputReader;
  private ServiceState state = new ServiceState();

  /** Control the server using input from <code>inputReader</code>. */
  protected ServiceController(InputStream inputReader) {
    this.inputReader = new BufferedReader(new InputStreamReader(System.in));
    state.set(true);
  }

  /**
   * Start reading commands.
   *
   * @throws IOException - if an I/O exception occurs while reading.
   * @throws IOException - if the service is interrupted prior to shutdown.
   */
  @Override
  public Void call() throws IOException, InterruptedServiceException {
    final int checkStateRatioMilliseconds = 1000;
    while (state.get() && isServiceRunning()) {
      String input = null;
      try {
        if (inputReader.ready()) {
          input = inputReader.readLine();
          if (input == null) {
            // EOF.
            state.set(false);
            break;
          }
        }
      } catch (IOException ioException) {
        if (state.get()) {
          throw ioException;
        }
      }
      if (input != null) {
        interpret(input);
      }
      try {
        Thread.sleep(checkStateRatioMilliseconds);
      } catch (InterruptedException exception) {
        if (state.get()) {
          throw new InterruptedServiceException(exception);
        }
      }
    }
    return null;
  }

  @Override
  public void shutdown() {
    state.set(false);
  }

  /** Interpret input. */
  protected abstract void interpret(String input);

  /**
   * Check if the service is running.
   *
   * @return true - if the service is running, false otherwise.
   */
  protected abstract boolean isServiceRunning();
}