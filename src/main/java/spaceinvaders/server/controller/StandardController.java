package spaceinvaders.server.controller;

import spaceinvaders.server.Server;
import spaceinvaders.utility.ServiceController;

/** Control the server by reading commands from stdin. */
public class StandardController extends ServiceController {
  private final Server server;

  public StandardController(Server server) {
    super(System.in);
    this.server = server;
  }

  @Override
  protected void interpret(String input) {
    /* Primitive switch. */
    switch (input) {
      case "quit":
        server.shutdown();
        break;
      default:
        // Do nothing.
        break;
    }
  }

  @Override
  protected boolean isServiceRunning() {
    return server.isRunning();
  }
}
