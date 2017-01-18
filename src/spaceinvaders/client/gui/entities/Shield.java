package spaceinvaders.client.gui.entities;

import java.io.IOException;
import spaceinvaders.game.GameConfig;

/** Shield block. */
public class Shield extends GraphicalEntity {
  public Shield() throws IOException {
    super(GameConfig.getInstance().getShieldImage());
  }
}
