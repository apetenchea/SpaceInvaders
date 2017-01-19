package spaceinvaders.server.game.world;

import spaceinvaders.game.EntityEnum;
import spaceinvaders.game.Entity;
import spaceinvaders.game.GameConfig;
import spaceinvaders.utility.Couple;

/** Bullet shot by a player. */
class PlayerBullet extends LogicEntity {
  private final GameConfig config = GameConfig.getInstance();

  public PlayerBullet(int posX, int posY) {
    super(EntityEnum.PLAYER_BULLET,posX,posY,
          GameConfig.getInstance().playerBullet().getWidth(),
          GameConfig.getInstance().playerBullet().getHeight());
  }
}
