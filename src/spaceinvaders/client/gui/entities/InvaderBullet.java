package spaceinvaders.client.gui.entities;

import spaceinvaders.exceptions.ResourceNotFoundException;
import spaceinvaders.game.GameConfig;

/**
 * Bullet fired by the invaders.
 */
public class InvaderBullet extends GraphicalEntity {
  public InvaderBullet() throws ResourceNotFoundException {
    super(GameConfig.getInstance().getInvaderBulletImage());
  }
}

