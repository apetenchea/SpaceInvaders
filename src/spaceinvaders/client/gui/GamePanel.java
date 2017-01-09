package spaceinvaders.client.gui;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Map;
import java.util.HashMap;
import java.util.LinkedHashMap;
import javax.swing.JPanel;
import javax.imageio.ImageIO;
import spaceinvaders.client.gui.entities.GraphicalEntity;
import spaceinvaders.client.gui.entities.GraphicsFactory;
import spaceinvaders.game.Entity;
import spaceinvaders.game.GameConfig;
import spaceinvaders.exceptions.ResourceNotFoundException;

/**
 * Main panel of the game.
 * <p> Contains all elements of the game including background, players, enemies and bullets.
 * Controls the painting and repainting.
 */
@SuppressWarnings("serial")
class GamePanel extends JPanel {
  private static final Logger LOGGER = Logger.getLogger(GamePanel.class.getName());

  private GameConfig config;
  private BufferedImage backgroundImage;
  private GraphicsFactory factory;
  private Map<Integer,GraphicalEntity> entitiesMap;
  private Map<Integer,String> playerNamesMap;
  
  public GamePanel() {
    config = GameConfig.getInstance();
    try {
      backgroundImage = ImageIO.read(new File(config.getGamePanelBackgroundImage()));
    } catch (IOException exception) {
      exception = new ResourceNotFoundException(exception);
      LOGGER.log(Level.SEVERE,exception.getMessage(),exception);
      LOGGER.severe(config.getGamePanelBackgroundImage());
    }
    factory = GraphicsFactory.getInstance();
    entitiesMap = new LinkedHashMap<>();
    playerNamesMap = new HashMap<>();
  }

  @Override
  protected void paintComponent(Graphics graphics) {
    super.paintComponent(graphics);

    graphics.drawImage(backgroundImage,0,0,null);
    for (Map.Entry<Integer,GraphicalEntity> entry : entitiesMap.entrySet()) {
      GraphicalEntity entity = entry.getValue();
      graphics.drawImage(entity.getImage(),entity.getX(),entity.getY(),this);
    }
    graphics.setColor(config.getGamePanelTextColor());
		graphics.setFont(config.getGamePanelTextFont());
    for (Map.Entry<Integer,String> entry : playerNamesMap.entrySet()) {
      GraphicalEntity player = entitiesMap.get(entry.getKey());
      if (player != null) {
        graphics.drawString(entry.getValue(),player.getX(),player.getY() + config.getPlayerHeight()
            + config.getPlayerNameOffset());
      }
    }
  }

  public void init() {
    entitiesMap.clear();
    playerNamesMap.clear();
  }

  public void addEntity(String type, Entity body) {
    GraphicalEntity entity = factory.create(type,body); 
    if (entity != null) {
      entitiesMap.put(entity.getId(),entity);
    }
  }

  public void moveEntity(int id, int newX, int newY) {
    GraphicalEntity entity = entitiesMap.get(id);
    if (entity == null) {
      LOGGER.warning("Trying to move inexistent entity " + id);
      return;
    }
    entity.move(newX,newY);
  }

  public void destroyEntity(int id) {
    GraphicalEntity entity = entitiesMap.get(id);
    if (entity == null) {
      LOGGER.warning("Trying to remove an inexistent entity " + id);
      return;
    }
    entitiesMap.remove(id);
  }

  public void addPlayer(Integer id, String name) {
    playerNamesMap.put(id,name);
  }
}
