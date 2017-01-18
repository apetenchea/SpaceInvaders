package spaceinvaders.server.game.world;

import spaceinvaders.game.Entity;
import spaceinvaders.game.GameConfig;
import spaceinvaders.utility.Couple;

public class Shield extends LogicEntity {
  private final GameConfig config = GameConfig.getInstance();

  public Shield(Entity entity) {
    super(entity,
        new Couple<Integer,Integer>(config.shield().getWidth(),config.shield().getHeight()));
  }
}
