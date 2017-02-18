package spaceinvaders.client.gui.entities;

/** Drawable entities. */
public interface Drawable {
  /** Draw this on the screen. */
  public void draw(GraphicalEntityVisitor visitor);
}
