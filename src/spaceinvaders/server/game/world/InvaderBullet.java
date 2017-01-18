package spaceinvaders.server.game.world;

import spaceinvaders.game.Entity;
import spaceinvaders.game.GameConfig;
import spaceinvaders.utility.Couple;

/** Bullet shot by an invader. */
public class InvaderBullet extends LogicEntity {
  private final GameConfig config = GameConfig.getInstance();

  public InvaderBullet(Entity entity) {
    super(entity,new Couple<Integer,Integer>(
          config.invaderBullet().getWidth(),config.invaderBullet().getHeight()));
  }
}
