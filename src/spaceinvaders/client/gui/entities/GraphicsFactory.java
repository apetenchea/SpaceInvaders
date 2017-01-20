package spaceinvaders.client.gui.entities;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Map;
import java.util.HashMap;
import spaceinvaders.exceptions.ResourceNotFoundException;
import spaceinvaders.game.Entity;
import spaceinvaders.game.EntityEnum;

/**
 * Creates graphical objects.
 */
public class GraphicsFactory {
  private static final Logger LOGGER = Logger.getLogger(GraphicsFactory.class.getName());
  private static GraphicsFactory singleton;

  private Map<EntityEnum,GraphicalEntity> entitiesMap;

  private GraphicsFactory() {
    entitiesMap = new HashMap<>();
    try {
      entitiesMap.put(EntityEnum.PLAYER,new Player());
      entitiesMap.put(EntityEnum.INVADER,new Invader());
      entitiesMap.put(EntityEnum.INVADER_BULLET,new InvaderBullet());
      entitiesMap.put(EntityEnum.PLAYER_BULLET,new PlayerBullet());
      entitiesMap.put(EntityEnum.SHIELD,new Shield());
    } catch (IOException ioException) {
      throw new AssertionError();
    }
  }

  public static GraphicsFactory getInstance() {
    if (singleton == null) {
      singleton = new GraphicsFactory();
    }
    return singleton;
  }

  public GraphicalEntity create(Entity entity) {
    GraphicalEntity value = entitiesMap.get(entity.getType());
    if (value != null) {
      value = (GraphicalEntity) value.clone();
      value.setEntity(entity);
      return value;
    }
    return null;
  }
}
