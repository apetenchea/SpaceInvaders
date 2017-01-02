package spaceinvaders.client.gui;

/**
 * Everything that appears on the screen.
 */
public interface GraphicalObject {
  /**
   * The object is no longer needed.
   */
  public void destroy();

  /**
   * Become invisible.
   */
  public void hide();

  /**
   * Become visible.
   */
  public void show();

}
