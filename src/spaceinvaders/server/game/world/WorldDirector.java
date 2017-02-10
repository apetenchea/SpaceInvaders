package spaceinvaders.server.game.world;

import java.util.List;

/** Create the game world. */
public class WorldDirector {
  private WorldBuilder builder;

  /**
   * @throws NullPointerException - if argument is {@code null}.
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
   * Assemble the {@link World}.
   */
  public void makeWorld(List<Integer> idList) {
    builder.buildInvaders();
    builder.buildPlayers(idList);
    builder.buildShields();
    builder.buildBullets();
  }
}
