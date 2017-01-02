package spaceinvaders;

import spaceinvaders.client.Client;

/**
 * The entry point of the application, instantiating either a {@link spaceinvaders.client.Client}
 * or a {@link spaceinvaders.server.Server}.
 */
public class SpaceInvaders {
  public static void main(String[] args) {
    (new Client()).run();
  }
}
