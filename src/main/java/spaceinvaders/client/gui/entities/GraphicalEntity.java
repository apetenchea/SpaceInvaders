package spaceinvaders.client.gui.entities;

import static java.util.logging.Level.SEVERE;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import spaceinvaders.Config;
import spaceinvaders.game.Entity;
import spaceinvaders.game.EntityEnum;

/** Game entity that can be drawn on the screen. */
public abstract class GraphicalEntity implements Cloneable, Drawable {
  private static final Logger LOGGER = Logger.getLogger(GraphicalEntity.class.getName());

  private final List<BufferedImage> avatar = new ArrayList<>(); 
  private Entity body;
  private Integer avatarNumber = 0;

  /**
   * Load all avatars for a graphical entity.
   *
   * @param imgPath list of paths to all images.
   *
   * @throws NullPointerException if argument is {@code null}.
   */
  protected GraphicalEntity(List<String> imgPath) {
    if (imgPath == null) {
      throw new NullPointerException();
    }
    final Config config = Config.getInstance();
    for (int index = 0; index < imgPath.size(); ++index) {
      try {
        avatar.add(config.getImageResource(imgPath.get(index)));
      } catch (IOException ioException) {
        LOGGER.severe("Image: " + imgPath.get(index));
        LOGGER.log(SEVERE,ioException.toString(),ioException);
      }
    }
  }

  @Override
  public Object clone() {
    try {
      return super.clone();
    } catch (CloneNotSupportedException exception) {
      // This should never happen.
      throw new AssertionError();
    }
  }

  public void setAvatarNumber(int avatarNumber) {
    this.avatarNumber = avatarNumber;
  }

  /**
   * Wrap around {@code body}.
   */
  public void setBody(Entity body) {
    this.body = body;
  }

  /**
   * Change the coordinates.
   * 
   * @param newX new coordinate on x-axis.
   * @param newY new coordinate on y-axis.
   */
  public void relocate(int newX, int newY) {
    body.setPos(newX,newY);
  }

  /**
   * Translate the entity in space.
   *
   * @param offsetX - offset on x-axis.
   * @param offsetY - offset on y-axis.
   */
  public void translate(int offsetX, int offsetY) {
    body.setPos(body.getX() + offsetX,body.getY() + offsetY);
  }

  public EntityEnum getType() {
    return body.getType();
  }

  /**
   * Get the avatar corresponding to {@code index}.
   *
   * @throws IndexOutOfBoundsException - if {@code index} is out of bounds. 
   */
  protected Image getAvatar(int index) {
    return avatar.get(index);
  }

  protected int getAvatarNumber() {
    return avatarNumber;
  }

  /**
   * @throws NullPointerException if the entity does not have a body.
   */
  protected int getX() {
    if (body == null) {
      throw new NullPointerException();
    }
    return body.getX();
  }

  /**
   * @throws NullPointerException - if the entity does not have a body.
   */
  protected int getY() {
    if (body == null) {
      throw new NullPointerException();
    }
    return body.getY();
  }
}
