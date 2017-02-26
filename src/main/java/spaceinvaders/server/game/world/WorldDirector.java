package spaceinvaders.server.game.world;

import java.util.List;

/** Create the game world. */
public class WorldDirector {
  private WorldBuilder builder;

  /**
   * @throws NullPointerException if argument is {@code null}.
   */
  public WorldDirector(WorldBuilder builder) {
    if (builder == null) {
      throw new NullPointerException();
    }
    this.builder = builder;
  }

  public World getWorld() {
    return builder.getWorld();
  }

  /**
   * Assemble the {@link spaceinvaders.server.game.world.World}.
   *
   * @param idList a list containting the ids of the human players which are in the built world.
   */
  public void makeWorld(List<Integer> idList) {
    builder.buildInvaders();
    builder.buildPlayers(idList);
    builder.buildShields();
    builder.buildBullets();
  }
}
