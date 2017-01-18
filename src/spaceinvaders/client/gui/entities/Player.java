package spaceinvaders.client.gui.entities;

import java.io.IOException;
import spaceinvaders.game.GameConfig;

/** The player character. */
public class Player extends GraphicalEntity {
  public Player() throws IOException {
    super(GameConfig.getInstance().getPlayerImage());
  }
}
