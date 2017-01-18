package spaceinvaders.server.game.world;

import spaceinvaders.game.Entity;
import spaceinvaders.game.GameConfig;
import spaceinvaders.utility.Couple;

public class Player extends LogicEntity {
  private final GameConfig config = GameConfig.getInstance();

  public Player(Entity entity) {
    super(entity,
        new Couple<Integer,Integer>(config.player().getWidth(),config.player().getHeight()));
  }
}
