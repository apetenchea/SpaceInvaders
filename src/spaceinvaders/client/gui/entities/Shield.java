package spaceinvaders.client.gui.entities;

import java.io.IOException;
import spaceinvaders.game.GameConfigOld;

/** Shield block. */
public class Shield extends GraphicalEntity {
  public Shield() throws IOException {
    super(GameConfigOld.getInstance().getShieldImage());
  }
}
