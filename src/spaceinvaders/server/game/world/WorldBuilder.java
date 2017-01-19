package spaceinvaders.server.game.world;

import java.util.List;

/** Defines methods needed to create parts of the world. */
interface WorldBuilder {
  public void buildInvaders();

  public void buildPlayers(int teamSize);

  public void buildShields();

  public void buildBullets();

  public World getWorld();
}
