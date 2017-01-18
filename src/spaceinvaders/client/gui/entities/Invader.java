package spaceinvaders.client.gui.entities;

import java.io.IOException;
import spaceinvaders.game.GameConfig;

/** The invader character. */
public class Invader extends GraphicalEntity {
  public Invader() throws IOException {
    super(GameConfig.getInstance().getInvaderImage());
  }
}
