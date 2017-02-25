package spaceinvaders.server.game.world;

import spaceinvaders.game.Entity;
import spaceinvaders.game.EntityEnum;

public class LogicEntity {
  private final Entity body;
  private final Integer width;
  private final Integer height;

  /**
   * @param type type of the entity.
   * @param posX the X coordinate.
   * @param posY the Y coordinate.
   * @param width the width of the entity.
   * @param height the height of the entity.
   *
   * @throws NullPointerException if an argument is {@code null}.
   */
  protected LogicEntity(EntityEnum type, int posX, int posY, int width, int height) {
    if (type == null) {
      throw new NullPointerException();
    }
    this.body = new Entity(type,posX,posY); 
    this.width = width;
    this.height = height;
  }

  /** Move the entity to a new position. */
  public void move(int newX, int newY) {
    body.setPos(newX,newY);
  }

  /**
   * Box collision detection.
   *
   * @return true if entities collide, false otherwise.
   */
  public boolean collides(LogicEntity entity) {
    return getX() < entity.getX() + entity.width
      && entity.getX() < getX() + width
      && getY() < entity.getY() + entity.height
      && entity.getY() < getY() + height;
  }

  /**
   * @return an {@link Entity} representation, which is the base level of representation.
   */
  public Entity getBase() {
    return body;
  }

  public EntityEnum getType() {
    return body.getType();
  }

  public int getX() {
    return body.getX();
  }

  public int getY() {
    return body.getY();
  }

  public int getId() {
    return body.getId();
  }

  protected void setId(int id) {
    body.setId(id);
  }
}
