package spaceinvaders.server.game.world;

import spaceinvaders.game.GameConfig;
import spaceinvaders.game.Entity;
import spaceinvaders.game.EntityEnum;
import spaceinvaders.utility.Couple;

/** Invader character. */
class Invader extends LogicEntity {
  private final GameConfig config = GameConfig.getInstance();

  public Invader(int posX, int posY) {
    super(EntityEnum.INVADER,posX,posY,
          GameConfig.getInstance().invader().getWidth(),
          GameConfig.getInstance().invader().getHeight());
  }
}
