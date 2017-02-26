package spaceinvaders.utility;

import java.util.concurrent.Semaphore;

/**
 * Used to delay an action by a number of milliseconds.
 *
 * <p>This is a switch which is initially off. After it waits for a given number of milliseconds
 * it turns on automatically. After the switch is turned off, the cycle starts again.
 *
 * <p>Turning the switch off while it is in the waiting stage has no effect.
 */
public class AutoSwitch implements Service<Void> {
  private final ServiceState running = new ServiceState();
  private final ServiceState switchState = new ServiceState();
  private final Semaphore mutex = new Semaphore(0);
  private Long rateMs = 1000L;

  public AutoSwitch() {}

  public AutoSwitch(long rateMs) {
    this.rateMs = rateMs;
  }

  /**
   * Start the cycle, with the switch initially turned off.
   *
   * @throws InterruptedException if interrupted prior to shutdown.
   */
  @Override
  public Void call() throws InterruptedException {
    running.set(true);
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
        mutex.acquire();
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

  /**
   * Toggle the switch and resume the cycle.
   *
   * <p>Turning the switch off multiple times, while it is already off, has the same effect as if
   * it was turned off only once.
   */
  public void toggle() {
    if (mutex.availablePermits() == 0) {
      boolean value = switchState.get();
      switchState.set(!value);
      mutex.release();
    }
  }

  public void setRate(long rateMs) {
    this.rateMs = rateMs;
  }
}
