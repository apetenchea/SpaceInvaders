package spaceinvaders.client.gui.entities;

import java.awt.image.BufferedImage;
import java.util.logging.Level;
import java.util.logging.Logger;
import spaceinvaders.game.Entity;

import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import spaceinvaders.exceptions.ResourceNotFoundException;

/**
 * Game entities that appear on the screen.
 */
public abstract class GraphicalEntity implements Cloneable {
  private static final Logger LOGGER = Logger.getLogger(GraphicalEntity.class.getName());
  private BufferedImage avatar; 
  private Entity entity;

  protected GraphicalEntity(String imagePath) throws ResourceNotFoundException {
    try {
      avatar = ImageIO.read(new File(imagePath));
    } catch (IOException exception) {
      throw new ResourceNotFoundException(exception);
    }
  }

  @Override
  public Object clone() {
    try {
      return super.clone();
    } catch (CloneNotSupportedException exception) {
      LOGGER.log(Level.SEVERE,exception.toString(),exception);
    }
    return null;
  }

  public void setEntity(Entity entity) {
    this.entity = entity;
  }

  public void move(int xCoord, int yCoord) {
    entity.move(xCoord,yCoord);
  }

  public BufferedImage getImage() {
    return avatar;
  }

   public int getId() {
    return entity.getId();
  }

  public int getX() {
    return entity.getX();
  }

  public int getY() {
    return entity.getY();
  }
}
