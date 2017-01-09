
package spaceinvaders.client.gui.entities;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import spaceinvaders.game.GameConfig;
import spaceinvaders.exceptions.ResourceNotFoundException;

/**
 * Bullet fired by the player.
 */
public class PlayerBullet extends GraphicalEntity {
  private static BufferedImage avatar; 

  public PlayerBullet() throws ResourceNotFoundException {
    try {
      avatar = ImageIO.read(new File(GameConfig.getInstance().getPlayerBulletImage()));
    } catch (IOException exception) {
      throw new ResourceNotFoundException(exception);
    }
  }

  @Override
  public BufferedImage getImage() {
    return avatar;
  }
}

