package spaceinvaders.client.gui.entities;

import static spaceinvaders.game.EntityEnum.SHIELD;

import spaceinvaders.client.ResourcesConfig;

/** Shield block. */
public class Shield extends GraphicalEntity {
  public Shield() {
    super(ResourcesConfig.getInstance().getAvatars(SHIELD));
  }

  @Override
  public void draw(GraphicalEntityVisitor visitor) {
    visitor.visit(this);
  }
}
