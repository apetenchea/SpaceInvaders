package spaceinvaders.game;

import java.awt.Point;

/**
 * Everything that exists in the game world.
 */
public class Entity {
  private Integer id;
  private Point pos;

  public Entity(int id, Point pos) {
    this.id = id;
    this.pos = pos;
  }
}
