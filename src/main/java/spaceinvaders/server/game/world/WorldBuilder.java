package spaceinvaders.server.game.world;

import java.util.List;

/** Defines methods needed to create parts of the world. */
interface WorldBuilder {
  public void buildInvaders();

  public void buildPlayers(List<Integer> idList);

  public void buildShields();

  public void buildBullets();

  /**
   * Get the last {@link spaceinvaders.server.game.world.World} built.
   */
  public World getWorld();
}
