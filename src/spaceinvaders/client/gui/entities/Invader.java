package spaceinvaders.client.gui.entities;

import spaceinvaders.exceptions.ResourceNotFoundException;
import spaceinvaders.game.GameConfig;

/**
 * The invader character.
 */
public class Invader extends GraphicalEntity {
  public Invader() throws ResourceNotFoundException {
    super(GameConfig.getInstance().getInvaderImage());
  }
}
