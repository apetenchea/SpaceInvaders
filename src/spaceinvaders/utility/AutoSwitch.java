package spaceinvaders.utility;

import java.util.concurrent.Semaphore;

/** Used to delay an action by a fixed number of milliseconds. */
public class AutoSwitch implements Service<Void> {
  private final ServiceState running = new ServiceState();
  private final ServiceState switchState = new ServiceState();
  private final Semaphore loop = new Semaphore(0);
  private Long rateMs;

  /** Construct an {@code AutoSwitch} with the rate set to 1000 Ms. */
  public AutoSwitch() {
    rateMs = 1000L;
    running.set(true);
  }

  public AutoSwitch(long rateMs) {
    this.rateMs = rateMs;
    running.set(true);
  }

  /**
   * Start with a delay, and then turn on the switch at a fixed rate, after each toggle.
   *
   * @throws InterruptedException - if interrupted prior to shutdown.
   */
  @Override
  public Void call() throws InterruptedException {
    while (running.get()) {
      try {
        Thread.sleep(rateMs);
      } catch (InterruptedException intException) {
        if (running.get()) {
          throw new InterruptedException();
        }
      }
      switchState.set(true);
      try {
        loop.acquire();
      } catch (InterruptedException intException) {
        if (running.get()) {
          throw new InterruptedException();
        }
        break;
      }
    }
    return null;
  }

  @Override
  public void shutdown() {
    running.set(false);
  }

  public boolean isOn() {
    return switchState.get();
  }

  /** Toggle the switch and resume the cycle. */
  public void toggle() {
    boolean value = switchState.get();
    switchState.set(!value);
    loop.release();
  }

  public long getRate() {
    return rateMs;
  }

  public void setRate(long rateMs) {
    this.rateMs = rateMs;
  }
}
