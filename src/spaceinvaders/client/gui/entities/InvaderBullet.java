package spaceinvaders.client.gui.entities;

import java.io.IOException;
import spaceinvaders.game.GameConfig;

/** Bullet fired by the invaders. */
public class InvaderBullet extends GraphicalEntity {
  public InvaderBullet() throws IOException {
    super(GameConfig.getInstance().getInvaderBulletImage());
  }
}

