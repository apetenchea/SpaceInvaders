package spaceinvaders.server.game.world;

import static spaceinvaders.game.EntityEnum.INVADER;

import spaceinvaders.game.GameConfig;

/** Invader character. */
public class Invader extends LogicEntity {
  /**
   * @param posX X coordinate.
   * @param posY Y coordinate.
   */
  public Invader(int posX, int posY) {
    super(INVADER,posX,posY,
          GameConfig.getInstance().invader().getWidth(),
          GameConfig.getInstance().invader().getHeight());
  }
}
