package spaceinvaders.server.game.world;

import spaceinvaders.game.Entity;
import spaceinvaders.game.EntityEnum;
import spaceinvaders.utility.Couple;

class LogicEntity {
  private final Entity body;
  private final Couple<Integer,Integer> size;

  public LogicEntity(Entity body, Couple<Integer,Integer> size) {
    this.body = body; 
    this.size = size;
  }

  /** Move the entity to a new position. */
  public void move(int newX, int newY) {
    Couple<Integer,Integer> pos = body.getPos();
    pos.setFirst(newX);
    pos.setSecond(newY);
  }

  /**
   * @return true if entities collide, false otherwise.
   */
  public boolean collides(LogicEntity entity) {
    return getX() <= entity.getX() + entity.size.getFirst()
      && entity.getX() <= getX() + size.getFirst()
      && getY() <= entity.getY() + entity.size.getSecond()
      && entity.getY() <= getY() + size.getSecond();
  }

  public Entity getEntity() {
    return body;
  }

  public EntityEnum getType() {
    return body.getType();
  }

  public int getX() {
    return body.getPos().getFirst();
  }

  public int getY() {
    return body.getPos().getSecond();
  }
}
