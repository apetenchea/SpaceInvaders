package spaceinvaders.client.gui.entities;

import java.awt.image.BufferedImage;
import java.util.logging.Logger;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import spaceinvaders.game.Entity;
import spaceinvaders.exceptions.ResourceNotFoundException;

/** Game entity that is drawable on the screen. */
public abstract class GraphicalEntity implements Cloneable {
  private static final Logger LOGGER = Logger.getLogger(GraphicalEntity.class.getName());

  private final BufferedImage avatar; 
  private Entity entity;

  protected GraphicalEntity(String imagePath) throws IOException {
    avatar = ImageIO.read(new File(imagePath));
  }

  @Override
  public Object clone() {
    try {
      return super.clone();
    } catch (CloneNotSupportedException exception) {
      throw new AssertionError();
    }
  }

  public void setEntity(Entity entity) {
    this.entity = entity;
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
