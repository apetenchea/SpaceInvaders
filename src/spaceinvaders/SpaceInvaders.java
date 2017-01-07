package spaceinvaders;

import java.util.logging.Level;
import java.util.logging.Logger;
import spaceinvaders.client.Client;
import spaceinvaders.server.Server;

/**
 * The entry point of the application, instantiating either a {@link spaceinvaders.client.Client}
 * or a {@link spaceinvaders.server.Server}.
 */
public class SpaceInvaders {
  private static final Logger LOGGER = Logger.getLogger(SpaceInvaders.class.getName());

  public static void main(String[] args) {
    if (args.length < 1) {
      LOGGER.info("Usage: " + args[0] + " help|client|server [options]");
    }
    if (args[0].equals("help")) {
      LOGGER.info("Help");
    } else if (args[0].equals("client")) {
      (new Client()).call();
    } else if (args[0].equals("server")) {
      (new Server(5412)).call();
    }
  }
}
