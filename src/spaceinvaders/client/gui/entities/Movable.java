package spaceinvaders.client.gui.entities;

/**
 * Movable graphical object.
 */
public interface Movable {
  public void moveUp(int pixels);

  public void moveRight(int pixels);

  public void moveDown(int pixels);

  public void moveLeft(int pixels);
}
