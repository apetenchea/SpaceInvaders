package spaceinvaders.client.gui.entities;

import static spaceinvaders.game.EntityEnum.INVADER;

import spaceinvaders.client.ResourcesConfig;

/** The invader character. */
public class Invader extends GraphicalEntity {
  public Invader() {
    super(ResourcesConfig.getInstance().getAvatars(INVADER));
  }

  @Override
  public void draw(GraphicalEntityVisitor visitor) {
    visitor.visit(this);
  }
}
