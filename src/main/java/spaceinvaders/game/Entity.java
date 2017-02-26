package spaceinvaders.game;

import spaceinvaders.utility.Couple;

/** Everything that the user can interact with. */
public class Entity implements Cloneable {
  private EntityEnum type;
  private Integer id;
  // Upper-left corner coordinates.
  private Couple<Integer,Integer> pos;

  /**
   * @param type the type of the entity.
   * @param id the id of the entity.
   * @param posX x coordinate.
   * @param posY y coordinate.
   *
   * @throws NullPointerException if an argument is {@code null}.
   */
  public Entity(EntityEnum type, int id, int posX, int posY) {
    if (type == null) {
      throw new NullPointerException();
    }
    this.type = type;
    this.id = id;
    this.pos = new Couple<>(posX,posY);
  }

  /**
   * @param type the type of the entity.
   * @param posX x coordinate.
   * @param posY y coordinate.
   *
   * @throws NullPointerException - if an argument is {@code null}.
   */
  public Entity(EntityEnum type, int posX, int posY) {
    if (type == null) {
      throw new NullPointerException();
    }
    this.type = type;
    id = hashCode();
    this.pos = new Couple<>(posX,posY);
  }

  @Override
  public Entity clone() throws CloneNotSupportedException {
    return (Entity) super.clone();
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public EntityEnum getType() {
    return type;
  }

  public int getX() {
    return pos.getFirst();
  }

  public int getY() {
    return pos.getSecond();
  }

  public void setPos(int posX, int posY) {
    pos.setFirst(posX);
    pos.setSecond(posY);
  }
}
