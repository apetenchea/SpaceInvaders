package spaceinvaders.game;

import spaceinvaders.utility.Couple;

/** Everything that exists in the game world. */
public class Entity {
  private EntityEnum type;
  private Integer id = hashCode();
  private Couple<Integer,Integer> pos;

  public Entity(Entity entity) {
    type = entity.type;
    id = entity.id;
    pos = entity.pos;
  }

  public Entity(EntityEnum type, int id, Couple <Integer,Integer> pos) {
    this.type = type;
    this.id = id;
    this.pos = pos;
  }

  public Entity(EntityEnum type, int xPos, int yPos) {
    this.type = type;
    id = hashCode();
    this.pos = new Couple<Integer,Integer>(xPos,yPos);
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

  public Couple<Integer,Integer> getPos() {
    return pos;
  }
}
