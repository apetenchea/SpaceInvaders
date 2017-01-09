package spaceinvaders.client.gui.entities;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import spaceinvaders.game.GameConfig;
import spaceinvaders.exceptions.ResourceNotFoundException;

/**
 * Bullet fired by the invaders.
 */
public class InvaderBullet extends GraphicalEntity {
  private static BufferedImage avatar; 

  public InvaderBullet() throws ResourceNotFoundException {
    try {
      avatar = ImageIO.read(new File(GameConfig.getInstance().getInvaderBulletImage()));
    } catch (IOException exception) {
      throw new ResourceNotFoundException(exception);
    }
  }

  @Override
  public BufferedImage getImage() {
    return avatar;
  }
}
