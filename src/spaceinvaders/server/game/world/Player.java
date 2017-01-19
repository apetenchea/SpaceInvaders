package spaceinvaders.server.game.world;

import spaceinvaders.game.Entity;
import spaceinvaders.game.EntityEnum;
import spaceinvaders.game.GameConfig;
import spaceinvaders.utility.Couple;

class Player extends LogicEntity {
  private final GameConfig config = GameConfig.getInstance();

  public Player(int posX, int posY) {
    super(EntityEnum.PLAYER,posX,posY,
          GameConfig.getInstance().player().getWidth(),
          GameConfig.getInstance().player().getHeight());
  }
}
