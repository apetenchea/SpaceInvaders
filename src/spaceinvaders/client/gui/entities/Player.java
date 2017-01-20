package spaceinvaders.client.gui.entities;

import java.io.IOException;
import spaceinvaders.game.GameConfigOld;

/** The player character. */
public class Player extends GraphicalEntity {
  public Player() throws IOException {
    super(GameConfigOld.getInstance().getPlayerImage());
  }
}
