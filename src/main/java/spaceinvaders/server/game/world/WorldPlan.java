package spaceinvaders.server.game.world;

import java.util.Iterator;
import java.util.List;
import spaceinvaders.game.EntityEnum;

/** Blueprint for the game world. */
interface WorldPlan {
  /**
   * Set all entities of a certain {@code type}, that populate the world.
   */
  public void setEntities(EntityEnum type, List<LogicEntity> entities);

  /**
   * Get an iterator for all entities of a given {@code type}.
   */
  public Iterator<LogicEntity> getIterator(EntityEnum type);
}
