package spaceinvaders.client.gui.entities;

import static spaceinvaders.game.EntityEnum.INVADER_BULLET;

import spaceinvaders.client.ResourcesConfig;

/** Bullet fired by the invaders. */
public class InvaderBullet extends GraphicalEntity {
  public InvaderBullet() {
    super(ResourcesConfig.getInstance().getAvatars(INVADER_BULLET));
  }

  @Override
  public void draw(GraphicalEntityVisitor visitor) {
    visitor.visit(this);
  }
}

