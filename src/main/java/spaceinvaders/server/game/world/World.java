package spaceinvaders.server.game.world;

import static spaceinvaders.game.EntityEnum.INVADER_BULLET;
import static spaceinvaders.game.EntityEnum.PLAYER_BULLET;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import spaceinvaders.game.Entity;
import spaceinvaders.game.EntityEnum;

/** Contains all characters that interact in the game. */
public class World implements WorldPlan {
  private Map<EntityEnum,List<LogicEntity>> entityMap = new HashMap<>();

  /**
   * @throws NullPointerException if an argument is {@code null}.
   */
  @Override
  public void setEntities(EntityEnum type, List<LogicEntity> entities) {
    if (type == null || entities == null) {
      throw new NullPointerException();
    }
    entityMap.put(type,entities);
  }

  /**
   * @return an iterator or {@code null} if the specified entity type could not be found.
   *
   * @throws NullPointerException if argument is {@code null}.
   */
  @Override
  public Iterator<LogicEntity> getIterator(EntityEnum type) {
    if (type == null) {
      throw new NullPointerException();
    }
    List<LogicEntity> list = entityMap.get(type);
    return (list == null ? null : list.iterator());
  }

  /**
   * Returns the number of entities of a given {@code type}.
   *
   * @throws NullPointerException if argument is {@code null}.
   */
  public int count(EntityEnum type) {
    if (type == null) {
      throw new NullPointerException();
    }
    List<LogicEntity> list = entityMap.get(type);
    return (list == null ? 0 : list.size());
  }

  /**
   * Create an {@link spaceinvaders.server.game.world.InvaderBullet}.
   *
   * @param bulletX X coordinate.
   * @param bulletY Y coordinate.
   *
   * @return the newly created bullet.
   */
  public LogicEntity spawnInvaderBullet(int bulletX, int bulletY) {
    List<LogicEntity> list = entityMap.get(INVADER_BULLET);
    if (list == null) {
      // This should never happen.
      throw new AssertionError();
    }
    LogicEntity bullet = new InvaderBullet(bulletX,bulletY);
    list.add(bullet);
    return bullet;
  }

  /**
   * Create a {@link spaceinvaders.server.game.world.PlayerBullet}.
   *
   * @param shooterId ID of the player who shot the bullet.
   * @param bulletX X coordinate.
   * @param bulletY Y coordinate.
   *
   * @return the newly created bullet.
   */
  public LogicEntity spawnPlayerBullet(int shooterId, int bulletX, int bulletY) {
    List<LogicEntity> list = entityMap.get(PLAYER_BULLET);
    if (list == null) {
      // This should never happen.
      throw new AssertionError();
    }
    LogicEntity bullet = new PlayerBullet(shooterId,bulletX,bulletY);
    list.add(bullet);
    return bullet;
  }

  /**
   * Get all entities.
   *
   * @return a list of all currently active entities.
   */
  public List<Entity> getEntities() {
    List<Entity> entities = new ArrayList<>();
    for (List<LogicEntity> value : entityMap.values()) {
      for (LogicEntity it : value) {
        entities.add(it.getBase());
      }
    }
    return entities;
  } 
}
