package spaceinvaders.server.game.world;

import spaceinvaders.game.Entity;
import spaceinvaders.game.GameConfig;
import spaceinvaders.utility.Couple;

/** Bullet shot by a player. */
public class PlayerBullet extends LogicEntity {
  private final GameConfig config = GameConfig.getInstance();

  public PlayerBullet(Entity entity) {
    super(entity,new Couple<Integer,Integer>(
          config.playerBullet().getWidth(),config.playerBullet().getHeight()));
  }
}
