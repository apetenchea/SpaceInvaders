package spaceinvaders.client.gui.entities;

import static spaceinvaders.game.EntityEnum.PLAYER_BULLET;

import spaceinvaders.client.ResourcesConfig;

/** Bullet fired by the player. */
public class PlayerBullet extends GraphicalEntity {
  public PlayerBullet() {
    super(ResourcesConfig.getInstance().getAvatars(PLAYER_BULLET));
  }

  @Override
  public void draw(GraphicalEntityVisitor visitor) {
    visitor.visit(this);
  }
}

