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
      System.err.println("Id : " + entity.getId() + " " + entity.getX() + " " + entity.getY());
      graphics.drawImage(entity.getImage(),entity.getX(),entity.getY(),this);
    }
    graphics.setColor(config.getGamePanelTextColor());
		graphics.setFont(config.getGamePanelTextFont());
    for (Map.Entry<Integer,String> entry : playerNamesMap.entrySet()) {
      GraphicalEntity player = entitiesMap.get(entry.getKey());
      System.err.println(player);
      graphics.drawString(entry.getValue(),player.getX(),player.getY() + config.getPlayerHeight()
          + config.getPlayerNameOffset());
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

  public void removeEntity(GraphicalEntity entity) {
    entitiesMap.remove(entity.getId());
  }

  public void addPlayer(Integer id, String name) {
    playerNamesMap.put(id,name);
  }
}
