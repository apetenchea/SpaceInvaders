package spaceinvaders.client.gui;

/**
 * A window that the user interacts with.
 */
public interface UiObject {
  /** Free resources and never use it again. */
  public void destroy();

  /** Become invisible. */
  public void hide();

  /** Become visible. */
  public void show();
}
