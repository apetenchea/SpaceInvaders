package spaceinvaders.server.game.world;

import spaceinvaders.game.GameConfig;
import spaceinvaders.game.Entity;
import spaceinvaders.utility.Couple;

public class Invader extends LogicEntity {
  private final GameConfig config = GameConfig.getInstance();

  public Invader(Entity entity) {
    super(entity,
        new Couple<Integer,Integer>(config.invader().getWidth(),config.invader().getHeight()));
  }
}
