
package spaceinvaders.client.gui.entities;

import spaceinvaders.game.GameConfig;
import spaceinvaders.exceptions.ResourceNotFoundException;

/**
 * Bullet fired by the player.
 */
public class PlayerBullet extends GraphicalEntity {
  public PlayerBullet() throws ResourceNotFoundException {
    super(GameConfig.getInstance().getPlayerBulletImage());
  }
}

