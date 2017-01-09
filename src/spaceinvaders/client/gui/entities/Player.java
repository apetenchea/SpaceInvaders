package spaceinvaders.client.gui.entities;

import spaceinvaders.game.GameConfig;
import spaceinvaders.exceptions.ResourceNotFoundException;

/**
 * The player character.
 */
public class Player extends GraphicalEntity {
  public Player() throws ResourceNotFoundException {
    super(GameConfig.getInstance().getPlayerImage());
  }
}
