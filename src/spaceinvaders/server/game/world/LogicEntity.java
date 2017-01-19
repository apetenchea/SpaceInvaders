package spaceinvaders.server.game.world;

import spaceinvaders.game.Entity;
import spaceinvaders.game.EntityEnum;
import spaceinvaders.utility.Couple;

public class LogicEntity {
  private final Entity body;
  private final Integer width;
  private final Integer height;

  public LogicEntity(EntityEnum type, int posX, int posY, int width, int height) {
    this.body = new Entity(type,posX,posY); 
    this.width = width;
    this.height = height;
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
    return getX() <= entity.getX() + entity.width
      && entity.getX() <= getX() + width
      && getY() <= entity.getY() + entity.height
      && entity.getY() <= getY() + width;
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

  public void setId(int id) {
    body.setId(id);
  }
}
