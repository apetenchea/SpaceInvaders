package spaceinvaders.server.game.world;

import static spaceinvaders.game.EntityEnum.INVADER_BULLET;

import spaceinvaders.game.GameConfig;

/** Bullet shot by an invader. */
public class InvaderBullet extends LogicEntity {
  /**
   * @param posX X coordinate.
   * @param posY Y coordinate.
   */
  public InvaderBullet(int posX, int posY) {
    super(INVADER_BULLET,posX,posY,
        GameConfig.getInstance().invaderBullet().getWidth(),
        GameConfig.getInstance().invaderBullet().getHeight());
  }
}
