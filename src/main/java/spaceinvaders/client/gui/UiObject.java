package spaceinvaders.client.gui;

/** A window with which the user interacts. */
public interface UiObject {
  /** Free resources and never use the window again. */
  public void destroy();

  /** Hide window. */
  public void hide();

  /** Show window. */
  public void show();
}
