package spaceinvaders.client.gui.entities;

import static java.util.logging.Level.SEVERE;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import spaceinvaders.game.Entity;

/** Game entity that can be drawn on the screen. */
public abstract class GraphicalEntity implements Cloneable, Drawable {
  private static final Logger LOGGER = Logger.getLogger(GraphicalEntity.class.getName());

  private final List<BufferedImage> avatar; 
  private Entity body;
  private Integer avatarNumber = 0;

  /**
   * Load all avatars for a graphical entity.
   *
   * @param imgPath - list of paths to all images.
   *
   * @throws NullPointerException - if argument is {@code null}.
   */
  protected GraphicalEntity(List<String> imgPath) {
    if (imgPath == null) {
      throw new NullPointerException();
    }
    for (int index = 0; index < imgPath.size(); ++index) {
      try {
        avatar.add(ImageIO.read(new File(imgPath.get(index))));
      } catch (IOException ioException) {
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
   * Get the avatar corresponding to the order in the {@code imgPath} provided in the constructor.
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
   * @throws NullPointerException - if the entity does not have any coordinates.
   */
  protected int getX() {
    if (body == null) {
      throw new NullPointerException();
    }
    return body.getX();
  }

  /**
   * @throws NullPointerException - if the entity does not have any coordinates.
   */
  protected int getY() {
    if (body == null) {
      throw new NullPointerException();
    }
    return body.getY();
  }
}
