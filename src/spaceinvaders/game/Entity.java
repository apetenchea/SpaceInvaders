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

  public int getId() {
    return id;
  }

  public EntityEnum getType() {
    return type;
  }

  public Couple<Integer,Integer> getPos() {
    return pos;
  }
}
