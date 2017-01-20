package spaceinvaders.server.game.world;

import java.util.List;
import spaceinvaders.game.EntityEnum;

/** Blueprint for the game world. */
interface WorldPlan {
  public void setEntities(EntityEnum type, List<LogicEntity> entities);
}
