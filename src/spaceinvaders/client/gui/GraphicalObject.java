package spaceinvaders.client.gui;

/**
 * Everything that can appear on the screen.
 */
public interface GraphicalObject {
  /**
   * Free resources.
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
