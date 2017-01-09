package spaceinvaders.client.gui.entities;

import spaceinvaders.game.GameConfig;
import spaceinvaders.exceptions.ResourceNotFoundException;

/**
 * Shield block.
 */
public class Shield extends GraphicalEntity {
  public Shield() throws ResourceNotFoundException {
    super(GameConfig.getInstance().getShieldImage());
  }
}
