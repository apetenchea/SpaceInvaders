
package spaceinvaders.client.gui.entities;

import java.io.IOException;
import spaceinvaders.game.GameConfig;

/** Bullet fired by the player. */
public class PlayerBullet extends GraphicalEntity {
  public PlayerBullet() throws IOException {
    super(GameConfig.getInstance().getPlayerBulletImage());
  }
}

