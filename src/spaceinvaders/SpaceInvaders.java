package spaceinvaders;

import static java.util.logging.Level.SEVERE;

import java.util.Enumeration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Handler;
import java.util.logging.LogManager;
import java.util.logging.Level;
import java.util.logging.Logger;
import spaceinvaders.client.Client;
import spaceinvaders.utility.ServiceController;
import spaceinvaders.server.controller.StandardController;
import spaceinvaders.server.Server;

/**
 * The entry point of the application, instantiating either a {@link spaceinvaders.client.Client}
 * or a {@link spaceinvaders.server.Server}.
 */
public class SpaceInvaders {
  private static final Logger LOGGER = Logger.getLogger(SpaceInvaders.class.getName());

  public static void main(String[] args) {
    setGlobalLoggingLevel(Level.FINE);
    if (args.length < 1) {
      LOGGER.info("Usage: " + args[0] + " help|client|server [options]");
    }
    switch (args[0]) {
      case "help":
        LOGGER.info("help");
        break;
      case "client":
        Client client = new Client();
        client.call();
        break;
      case "server":
        try {
          Server server = new Server(5412);
          ServiceController controller = new StandardController(server);
          ExecutorService controllerExecutor = Executors.newSingleThreadExecutor();
          Future<Void> controllerFuture = controllerExecutor.submit(controller);
          setGlobalLoggingLevel(Level.FINE);
          server.call();
          controllerFuture.get();
          controllerExecutor.shutdownNow();
        } catch (Exception exception) {
          LOGGER.log(SEVERE,exception.toString(),exception);
        }
        break;
      default:
        LOGGER.info("Unkown argument: " + args[0]);
        break;
    }
  }

  private static void setGlobalLoggingLevel(Level level) {
    LogManager manager = LogManager.getLogManager();
    Enumeration<String> loggers = manager.getLoggerNames(); 
    while (loggers.hasMoreElements()) {
      Logger logger = manager.getLogger(loggers.nextElement());
      logger.setLevel(level);
      for (Handler handler : logger.getHandlers()) {
        handler.setLevel(level);
      }
    }
  }
}
