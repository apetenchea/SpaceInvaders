package spaceinvaders.server.game.world;

import static spaceinvaders.game.EntityEnum.SHIELD;

import spaceinvaders.game.GameConfig;

/** A shield protecting the player. */
public class Shield extends LogicEntity {
  /**
   * @param posX X coordinate.
   * @param posY Y coordinate.
   */
  public Shield(int posX, int posY) {
    super(SHIELD,posX,posY,
          GameConfig.getInstance().shield().getWidth(),
          GameConfig.getInstance().shield().getHeight());
  }
}
