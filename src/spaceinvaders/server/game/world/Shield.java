package spaceinvaders.server.game.world;

import static spaceinvaders.game.EntityEnum.SHIELD;

import spaceinvaders.game.Entity;
import spaceinvaders.game.GameConfig;

/** A shield protecting the player. */
class Shield extends LogicEntity {
  private final GameConfig config = GameConfig.getInstance();

  /**
   * @param posX - X coordinate.
   * @param posY - Y coordinate.
   */
  public Shield(int posX, int posY) {
    super(SHIELD,posX,posY,
          GameConfig.getInstance().shield().getWidth(),
          GameConfig.getInstance().shield().getHeight());
  }
}
