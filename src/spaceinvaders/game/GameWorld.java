package spaceinvaders.game;

import java.util.List;

/**
 * Represents the configuration of the game world.
 */
public abstract class GameWorld implements Cloneable {
  /*
   * Values are in pixels.
   */
  private Integer height;
  private Integer width;
  private Integer boxSize;
  private List<Entity> entities;

  public GameWorld clone() throws CloneNotSupportedException {
    return (GameWorld) super.clone();
  }

  public void addEntity (Entity entity) {
    entities.add(entity);
  }

  public int getHeight() {
    return height;
  }

  public void setHeight(int height) {
    this.height = height;
  }

  public int getWidth() {
    return width;
  }

  public void setWidth(int width) {
    this.width = width;
  }

  public int getBoxSize() {
    return boxSize;
  }

  public void setBoxSize(int boxSize) {
    this.boxSize = boxSize;
  }

  public List<Entity> getEntities() {
    return entities;
  }

  public void setEntities(List<Entity> entities) {
    this.entities = entities;
  }
}
