package spaceinvaders.client.gui.entities;

import java.awt.image.BufferedImage;
import java.util.logging.Level;
import java.util.logging.Logger;
import spaceinvaders.game.Entity;

/**
 * Game entities that appear on the screen.
 */
public abstract class GraphicalEntity implements Cloneable, Movable {
  private static final Logger LOGGER = Logger.getLogger(GraphicalEntity.class.getName());
  private Entity entity;

  @Override
  public Object clone() {
    try {
      return super.clone();
    } catch (CloneNotSupportedException exception) {
      LOGGER.log(Level.SEVERE,exception.toString(),exception);
    }
    return null;
  }

  @Override
  public void moveUp(int pixels) {
    entity.move(getX(),getY() - pixels); 
  }

  @Override
  public void moveRight(int pixels) {
    entity.move(getX() + pixels,getY());
  }

  @Override
  public void moveDown(int pixels) {
    entity.move(getX(),getY() + pixels);
  }

  @Override
  public void moveLeft(int pixels) {
    entity.move(getX() - pixels,getY());
  }

  public void setEntity(Entity entity) {
    this.entity = entity;
  }

  public abstract BufferedImage getImage();

   public int getId() {
    return entity.getId();
  }

  public int getX() {
    return entity.getX();
  }

  public int getY() {
    return entity.getY();
  }

  public void setPos(int xCoord, int yCoord) {
    entity.move(xCoord,yCoord);
  }
}
