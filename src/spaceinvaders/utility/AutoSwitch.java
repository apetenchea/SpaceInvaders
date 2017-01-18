package spaceinvaders.utility;

/** A switch that automatically turns on, at fixed rate. */
public class AutoSwitch implements Service<Void> {
  private final ServiceState running = new ServiceState();
  private final ServiceState switchState = new ServiceState();
  private final Long rateMs;

  public AutoSwitch(long rateMs) {
    this.rateMs = rateMs;
    running.set(true);
    switchState.set(false);
  }

 /**
  * Start with a delay, and then turn on the switch at a fixed rate.
  *
  * @throws InterruptedException - if interrupted prior to shutdown.
  */
  @Override
  public Void call() throws InterruptedException {
    while (running.get()) {
      try {
        Thread.sleep(rateMs);
      } catch (InterruptedException interruptedException) {
        if (running.get()) {
          throw interruptedException;
        }
      }
      switchState.set(true);
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

  public void toggle() {
    boolean value = switchState.get();
    switchState.set(!value);
  }
}
