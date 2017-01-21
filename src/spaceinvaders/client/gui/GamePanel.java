package spaceinvaders.client.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.logging.Logger;
import javax.swing.JPanel;
import spaceinvaders.client.gui.entities.GraphicalEntity;
import spaceinvaders.client.gui.entities.GraphicsFactory;
import spaceinvaders.game.Entity;
import spaceinvaders.game.GameConfigOld;

/**
 * Main panel of the game.
 * <p> Contains all elements of the game including background, players, enemies and bullets.
 * Controls the painting and repainting.
 */
@SuppressWarnings("serial")
class GamePanel extends JPanel {
  private static final Logger LOGGER = Logger.getLogger(GamePanel.class.getName());

  private final GameConfigOld config = GameConfigOld.getInstance();
  private final GraphicsFactory factory = GraphicsFactory.getInstance();
  private final Map<Integer,String> playerNamesMap = new HashMap<>();
  private final List<GraphicalEntity> entities = new ArrayList<>();
  
  public GamePanel() {
    setBackground(Color.BLACK);
    setForeground(config.getGamePanelForegroundColor());
  }

  @Override
  protected void paintComponent(Graphics graphics) {
    super.paintComponent(graphics);

    graphics.setColor(config.getGamePanelTextColor());
    graphics.setFont(config.getGamePanelTextFont());

    for (GraphicalEntity entity : entities) {
      graphics.drawImage(entity.getImage(),entity.getX(),entity.getY(),this);
      String name = playerNamesMap.get(entity.getId());
      if (name != null) {
        graphics.drawString(name,entity.getX(),entity.getY() + config.getPlayerHeight()
            + config.getPlayerNameOffset());
      }
    }
  }

  public void init() {
    playerNamesMap.clear();
  }

  public void refreshEntities(List<Entity> updates) {
    entities.clear();
    for (Entity update : updates) {
      GraphicalEntity entity = factory.create(update); 
      entities.add(entity);
    }
  }

  public void addPlayer(Integer id, String name) {
    playerNamesMap.put(id,name);
  }
}
