package spaceinvaders.client.gui;

/** A window with which the user interacts. */
public interface UiObject {
  /** Free resources and never use the window again. */
  public void destroy();

  /** Become invisible. */
  public void hide();

  /** Become visible. */
  public void show();
}
