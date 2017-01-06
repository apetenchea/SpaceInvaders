package spaceinvaders;

import spaceinvaders.client.Client;
import spaceinvaders.server.Server;

/**
 * The entry point of the application, instantiating either a {@link spaceinvaders.client.Client}
 * or a {@link spaceinvaders.server.Server}.
 */
public class SpaceInvaders {
  public static void main(String[] args) throws Exception {
    //(new Client()).call();
    (new Server(5412)).call();
  }
}
