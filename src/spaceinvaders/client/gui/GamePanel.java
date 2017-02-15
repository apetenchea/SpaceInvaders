package spaceinvaders.client.gui;

import java.awt.Color;
import java.awt.Font;
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
import spaceinvaders.game.EntityEnum;
import spaceinvaders.game.GameConfig;

/**
 * Main panel of the game.
 *
 * <p> Contains all visible elements of the game. Controls the painting and repainting.
 */
@SuppressWarnings("serial")
class GamePanel extends JPanel {
  private static final Logger LOGGER = Logger.getLogger(GamePanel.class.getName());

  private final GameConfig config = GameConfig.getInstance();
  private final GraphicsFactory factory = GraphicsFactory.getInstance();
  private final Map<Integer,String> playerNamesMap = new HashMap<>();
  private final List<GraphicalEntity> entities = new ArrayList<>();
  
  public GamePanel() {
    setBackground(Color.BLACK);
    setForeground(Color.BLACK);
  }

  @Override
  protected void paintComponent(Graphics graphics) {
    super.paintComponent(graphics);

    graphics.setColor(Color.WHITE);
    graphics.setFont(new Font("Courier",Font.BOLD,15));

    for (GraphicalEntity entity : entities) {
      graphics.drawImage(entity.getImage(),entity.getX(),entity.getY(),this);
      String name = playerNamesMap.get(entity.getId());
      if (name != null) {
        graphics.drawString(name,entity.getX(),entity.getY() + config.player().getHeight()
            + config.player().getHeight() / 4);
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

  public void moveEntity(int entityId, int newX, int newY) {
    // TODO
  }

  public void spawnEntity(int id, EntityEnum type, int posX, int posY) {
    // TODO
  }

  public void wipeOutEntity(int id) {
    // TODO
  }

  public void translateGroup(EntityEnum type, int offsetX, int offsetY) {
    // TODO
  }

  public void addPlayer(Integer id, String name) {
    playerNamesMap.put(id,name);
  }
}
