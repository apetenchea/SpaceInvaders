package spaceinvaders.utility;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Used to get and set the on/off state of multithreaded services.
 */
public class ServiceState {
  private AtomicBoolean state;
  private ReadWriteLock stateLock;

  /**
   * Construct a ServiceState having the provided state.
   */
  public ServiceState(boolean initialState) {
    state = new AtomicBoolean(initialState);
    stateLock = new ReentrantReadWriteLock();
  }

  /**
   * Set the state of the service.
   */
  public void set(boolean flag) {
    stateLock.writeLock().lock();
    state.set(flag);
    stateLock.writeLock().unlock();
  }

  /**
   * Get the state of the service.
   */
  public boolean get() {
    boolean result;
    stateLock.readLock().lock();
    result = state.get();
    stateLock.readLock().unlock();
    return result;
  }
}
