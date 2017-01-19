package spaceinvaders.server.game.world;

import java.util.List;

/** Create the game world. */
public class WorldDirector {
  private WorldBuilder builder;

  /**
   * @throws NullPointerException - if {@code builder} is {@code null}.
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

  public void makeWorld(int teamSize) {
    builder.buildInvaders();
    builder.buildPlayers(teamSize);
    builder.buildShields();
    builder.buildBullets();
  }
}
