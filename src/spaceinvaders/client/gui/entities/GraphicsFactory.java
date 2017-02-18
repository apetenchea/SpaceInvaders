package spaceinvaders.client.gui.entities;

import static spaceinvaders.game.EntityEnum.INVADER;
import static spaceinvaders.game.EntityEnum.INVADER_BULLET;
import static spaceinvaders.game.EntityEnum.PLAYER;
import static spaceinvaders.game.EntityEnum.PLAYER_BULLET;
import static spaceinvaders.game.EntityEnum.SHIELD;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import spaceinvaders.game.Entity;
import spaceinvaders.game.EntityEnum;

/** Creates graphical objects. */
public class GraphicsFactory {
  private static GraphicsFactory singleton;

  private Map<EntityEnum,GraphicalEntity> entitiesMap = new HashMap<>();

  private GraphicsFactory() {
    entitiesMap.put(PLAYER,new Player());
    entitiesMap.put(INVADER,new Invader());
    entitiesMap.put(INVADER_BULLET,new InvaderBullet());
    entitiesMap.put(PLAYER_BULLET,new PlayerBullet());
    entitiesMap.put(SHIELD,new Shield());
    entitiesMap = Collections.unmodifiableMap(entitiesMap);
  }

  /** Singleton. */
  public static GraphicsFactory getInstance() {
    if (singleton == null) {
      singleton = new GraphicsFactory();
    }
    return singleton;
  }

  /**
   * Evolve an {@link Entity} into a {@link GraphicalEntity}.
   *
   * @param entity - the base entity.
   *
   * @return - the {@link GraphicalEntity} wrapped around the {@code Entity}.
   *
   * @throws NullPointerException - if the argument is {@code null} or the entity cannot be created.
   */
  public GraphicalEntity create(Entity entity) {
    GraphicalEntity value = entitiesMap.get(entity.getType());
    if (value == null) {
      // This should never happen.
      throw new NullPointerException();
    }
    value = (GraphicalEntity) value.clone();
    value.setBody(entity);
    return value;
  }
}
