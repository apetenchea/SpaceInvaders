package spaceinvaders.client.gui.entities;

import java.awt.Graphics;

/** Drawable entities. */
public interface Drawable {
  /** Draw this on the screen. */
  public void draw(DrawingVisitor visitor);
}
