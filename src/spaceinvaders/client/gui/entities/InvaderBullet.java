package spaceinvaders.client.gui.entities;

import java.io.IOException;
import spaceinvaders.game.GameConfigOld;

/** Bullet fired by the invaders. */
public class InvaderBullet extends GraphicalEntity {
  public InvaderBullet() throws IOException {
    super(GameConfigOld.getInstance().getInvaderBulletImage());
  }
}

