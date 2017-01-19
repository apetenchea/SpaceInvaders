package spaceinvaders.server.game.world;

import spaceinvaders.game.Entity;
import spaceinvaders.game.EntityEnum;
import spaceinvaders.game.GameConfig;
import spaceinvaders.utility.Couple;

class Shield extends LogicEntity {
  private final GameConfig config = GameConfig.getInstance();

  public Shield(int posX, int posY) {
    super(EntityEnum.SHIELD,posX,posY,
          GameConfig.getInstance().shield().getWidth(),
          GameConfig.getInstance().shield().getHeight());
  }
}
