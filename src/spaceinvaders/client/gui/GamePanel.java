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
import spaceinvaders.game.GameConfig;

/**
 * Main panel of the game.
 * <p> Contains all elements of the game including background, players, enemies and bullets.
 * Controls the painting and repainting.
 */
@SuppressWarnings("serial")
class GamePanel extends JPanel {
  private static final Logger LOGGER = Logger.getLogger(GamePanel.class.getName());

  private final GameConfig config = GameConfig.getInstance();
  private final GraphicsFactory factory = GraphicsFactory.getInstance();
  private final Map<Integer,String> playerNamesMap = new HashMap<>();
  private final List<GraphicalEntity> entities = new ArrayList<>();
  
  public GamePanel() {
    setBackground(config.getGameBackgroundColor());
    setForeground(config.getGameForegroundColor());
  }

  @Override
  protected void paintComponent(Graphics graphics) {
    super.paintComponent(graphics);

    for (GraphicalEntity entity : entities) {
      graphics.drawImage(entity.getImage(),entity.getX(),entity.getY(),this);
    }
    /*
    graphics.setColor(config.getGamePanelTextColor());
		graphics.setFont(config.getGamePanelTextFont());
    for (Map.Entry<Integer,String> entry : playerNamesMap.entrySet()) {
      GraphicalEntity player = entitiesMap.get(entry.getKey());
      if (player != null) {
        graphics.drawString(entry.getValue(),player.getX(),player.getY() + config.getPlayerHeight()
            + config.getPlayerNameOffset());
      }
    }
    */
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
