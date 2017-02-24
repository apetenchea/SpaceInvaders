package spaceinvaders.utility;

/** 2-tuple. */
public class Couple<T1,T2> {
  private T1 first;
  private T2 second;

  public Couple(T1 first, T2 second) {
    this.first = first;
    this.second = second;
  }

  public T1 getFirst() {
    return first;
  }

  public void setFirst(T1 first) {
    this.first = first;
  }

  public T2 getSecond() {
    return second;
  }

  public void setSecond(T2 second) {
    this.second = second;
  }
}
