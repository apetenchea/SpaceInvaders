
package spaceinvaders.client.gui.entities;

import java.io.IOException;
import spaceinvaders.game.GameConfigOld;

/** Bullet fired by the player. */
public class PlayerBullet extends GraphicalEntity {
  public PlayerBullet() throws IOException {
    super(GameConfigOld.getInstance().getPlayerBulletImage());
  }
}

