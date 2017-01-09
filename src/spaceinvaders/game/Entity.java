package spaceinvaders.game;

import spaceinvaders.utility.Couple;

/**
 * Everything that exists in the game world.
 */
public class Entity {
  private Integer id;
  private Couple<Integer,Integer> pos;

  public Entity(int id, Couple<Integer,Integer> pos) {
    this.id = id;
    this.pos = pos;
  }

  public void move(int newX, int newY) {
    pos.setFirst(newX);
    pos.setSecond(newY);
  }

  public int getId() {
    return id;
  }

  public Couple<Integer,Integer> getPos() {
    return pos;
  }
  
  public int getX() {
    return pos.getFirst();
  }

  public int getY() {
    return pos.getSecond();
  }
}
