package spaceinvaders.server.game.world;

import java.util.List;
import spaceinvaders.game.EntityEnum;

/** What will be returned from the builder. */
interface WorldPlan {
  public void setEntities(EntityEnum type, List<LogicEntity> entities);
}
