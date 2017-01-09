package spaceinvaders.client.gui.entities;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import spaceinvaders.exceptions.ResourceNotFoundException;
import spaceinvaders.game.GameConfig;

/**
 * The invader character.
 */
public class Invader extends GraphicalEntity {
  private static BufferedImage avatar; 

  public Invader() throws ResourceNotFoundException {
    try {
      avatar = ImageIO.read(new File(GameConfig.getInstance().getInvaderImage()));
    } catch (IOException exception) {
      throw new ResourceNotFoundException(exception);
    }
  }

  @Override
  public BufferedImage getImage() {
    return avatar;
  }
}
