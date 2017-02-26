package spaceinvaders.utility;

import java.util.concurrent.Callable;

/** A service, usually used concurrently. */
public interface Service<T> extends Callable<T> {
  /** Stop this service and all other services started by it. */
  public void shutdown();
}
