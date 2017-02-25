package spaceinvaders.server.game.world;

import static spaceinvaders.game.EntityEnum.PLAYER_BULLET;

import spaceinvaders.game.GameConfig;

/** Bullet shot by a player. */
public class PlayerBullet extends LogicEntity {
  private final Integer shooterId;

  /**
   * @param shooterId id of the player who shot the bullet.
   * @param posX X coordinate.
   * @param posY Y coordinate.
   */
  public PlayerBullet(int shooterId, int posX, int posY) {
    super(PLAYER_BULLET,posX,posY,
          GameConfig.getInstance().playerBullet().getWidth(),
          GameConfig.getInstance().playerBullet().getHeight());
    this.shooterId = shooterId;
  }

  public int getShooterId() {
    return shooterId;
  }
}
