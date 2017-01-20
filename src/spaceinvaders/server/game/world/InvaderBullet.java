package spaceinvaders.server.game.world;

import spaceinvaders.game.Entity;
import spaceinvaders.game.EntityEnum;
import spaceinvaders.game.GameConfig;
import spaceinvaders.utility.Couple;

/** Bullet shot by an invader. */
public class InvaderBullet extends LogicEntity {
  private final GameConfig config = GameConfig.getInstance();

  public InvaderBullet(int posX, int posY) {
    super(EntityEnum.INVADER_BULLET,posX,posY,
          GameConfig.getInstance().invaderBullet().getWidth(),
          GameConfig.getInstance().invaderBullet().getHeight());
  }
}
