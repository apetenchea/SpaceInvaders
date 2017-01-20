package spaceinvaders.client.gui.entities;

import java.io.IOException;
import spaceinvaders.game.GameConfigOld;

/** The invader character. */
public class Invader extends GraphicalEntity {
  public Invader() throws IOException {
    super(GameConfigOld.getInstance().getInvaderImage());
  }
}
