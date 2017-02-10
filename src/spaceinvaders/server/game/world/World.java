package spaceinvaders.server.game.world;

import static spaceinvaders.game.EntityEnum.INVADER_BULLET;
import static spaceinvaders.game.EntityEnum.PLAYER_BULLET;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import spaceinvaders.game.EntityEnum;

/** Holds all characters that interact in the game. */
public class World implements WorldPlan {
  private Map<EntityEnum,List<LogicEntity>> entityMap = new HashMap<>();

  /**
   * @throws NullPointerException - if an argument is {@code null}.
   */
  @Override
  public void setEntities(EntityEnum type, List<LogicEntity> invaders) {
    if (type == null || invaders == null) {
      throw new NullPointerException();
    }
    entityMap.put(type,invaders);
  }

  /**
   * Returns an iterator over the entities of the given {@code type}.
   *
   * @return an iterator or {@code null} if the specified entity type could not be found.
   *
   * @throws NullPointerException - if argument is {@code null}.
   */
  public Iterator<LogicEntity> getIterator(EntityEnum type) {
    if (type == null) {
      throw new NullPointerException();
    }
    List<LogicEntity> list = entityMap.get(type);
    return (list == null ? null : list.iterator());
  }

  /**
   * Returns the number of entities of a given type.
   *
   * @throws NullPointerException - if argument is {@code null}.
   */
  public int count(EntityEnum type) {
    if (type == null) {
      throw new NullPointerException();
    }
    List<LogicEntity> list = entityMap.get(type);
    return (list == null ? 0 : list.size());
  }

  /**
   * @param bulletX - X coordinate.
   * @param bulletY - Y coordinate.
   */
  public void spawnInvaderBullet(int bulletX, int bulletY) {
    List<LogicEntity> list = entityMap.get(INVADER_BULLET);
    if (list == null) {
      // This should never happen.
      throw new AssertionError();
    }
    list.add(new InvaderBullet(bulletX,bulletY));
  }

  /**
   * @param shooterId - ID of the player who shot the bullet.
   * @param bulletX - X coordinate.
   * @param bulletY - Y coordinate.
   */
  public void spawnPlayerBullet(int shooterId, int bulletX, int bulletY) {
    List<LogicEntity> list = entityMap.get(PLAYER_BULLET);
    if (list == null) {
      // This should never happen.
      throw new AssertionError();
    }
    list.add(new PlayerBullet(shooterId,bulletX,bulletY));
  }
}
