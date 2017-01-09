package spaceinvaders.client.gui.entities;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Map;
import java.util.HashMap;
import spaceinvaders.exceptions.ResourceNotFoundException;
import spaceinvaders.game.Entity;

/**
 * Creates graphical objects.
 */
public class GraphicsFactory {
  private static final Logger LOGGER = Logger.getLogger(GraphicsFactory.class.getName());
  private static GraphicsFactory singleton;

  private Map<String,GraphicalEntity> entitiesMap;

  private GraphicsFactory() {
    entitiesMap = new HashMap<>();
    try {
      entitiesMap.put(Player.class.getName(),new Player());
      entitiesMap.put(Invader.class.getName(),new Invader());
      entitiesMap.put(InvaderBullet.class.getName(),new InvaderBullet());
      entitiesMap.put(PlayerBullet.class.getName(),new PlayerBullet());
      entitiesMap.put(Shield.class.getName(),new Shield());
    } catch (ResourceNotFoundException exception) {
      LOGGER.log(Level.SEVERE,exception.getMessage(),exception);
    }
  }

  public static GraphicsFactory getInstance() {
    if (singleton == null) {
      singleton = new GraphicsFactory();
    }
    return singleton;
  }

  public GraphicalEntity create(String type, Entity entity) {
    GraphicalEntity value = entitiesMap.get(type);
    if (value != null) {
      value = (GraphicalEntity) value.clone();
      value.setEntity(entity);
      return value;
    }
    return null;
  }
}
