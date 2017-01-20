package spaceinvaders.server.game.world;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import spaceinvaders.game.EntityEnum;

/** Holds all characters that interact in the game. */
public class World implements WorldPlan {
  private Map<EntityEnum,List<LogicEntity>> entityMap = new HashMap<>();

  @Override
  public void setEntities(EntityEnum type, List<LogicEntity> invaders) {
    entityMap.put(type,invaders);
  }

  /**
   * Returns an iterator over the entities of the given {@code type}.
   *
   * @return an iterator or {@code null} if the specified entity type could not be found.
   *
   * @throws NullPointerException - if {@code type} is {@code null}.
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
   * @throws NullPointerException - if {@code type} is {@code null}.
   */
  public int count(EntityEnum type) {
    if (type == null) {
      throw new NullPointerException();
    }
    List<LogicEntity> list = entityMap.get(type);
    return (list == null ? 0 : list.size());
  }

  public void spawnInvaderBullet(int bulletX, int bulletY) {
    List<LogicEntity> list = entityMap.get(EntityEnum.INVADER_BULLET);
    if (list == null) {
      throw new AssertionError();
    }
    list.add(new InvaderBullet(bulletX,bulletY));
  }

  public void spawnPlayerBullet(int shooterId, int bulletX, int bulletY) {
    List<LogicEntity> list = entityMap.get(EntityEnum.PLAYER_BULLET);
    if (list == null) {
      throw new AssertionError();
    }
    list.add(new PlayerBullet(shooterId,bulletX,bulletY));
  }
}
