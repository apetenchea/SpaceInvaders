package spaceinvaders.utility;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Used to hold the on/off state of a service.
 *
 * <p>The implementation is thread-safe.
 */
public class ServiceState {
  private AtomicBoolean state;
  private ReadWriteLock stateLock;

  /** Construct a state variable initially set to false. */
  public ServiceState() {
    state = new AtomicBoolean();
    stateLock = new ReentrantReadWriteLock();
  }

  /** Construct a state variable having the given initial state. */
  public ServiceState(boolean initialState) {
    state = new AtomicBoolean(initialState);
    stateLock = new ReentrantReadWriteLock();
  }

  /** Get the value of the state variable. */
  public boolean get() {
    boolean result;
    stateLock.readLock().lock();
    result = state.get();
    stateLock.readLock().unlock();
    return result;
  }

  /** Set value of the state variable. */
  public void set(boolean flag) {
    stateLock.writeLock().lock();
    state.set(flag);
    stateLock.writeLock().unlock();
  }
}
