package spaceinvaders;

import static java.util.logging.Level.INFO;
import static java.util.logging.Level.SEVERE;

import java.util.Enumeration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import spaceinvaders.client.Client;
import spaceinvaders.server.Server;
import spaceinvaders.server.controller.StandardController;
import spaceinvaders.utility.ServiceController;

/**
 * The entry point of the application, instantiating either a {@link spaceinvaders.client.Client}
 * or a {@link spaceinvaders.server.Server}.
 */
public class SpaceInvaders {
  private static final Logger LOGGER = Logger.getLogger(SpaceInvaders.class.getName());

  /**
   * Parse {@code args} and start a client, a server, or display help.
   */
  public static void main(String[] args) {
    final String help =
        "Usage:\n\n"
          + "\thelp - Display this help.\n"
            + "\t\tExample: java -jar space-invaders-*.jar help\n\n"
          + "\tclient - Start client.\n"
            + "\t\tExample:  java -jar space-invaders-*.jar client\n"
          + "\tserver port - Start a server on this machine, using the specified port.\n\n"
            + "\t\tExample of starting a server on port 5412: java -jar space-invaders.*.jar server 5412\n\n"
          + "\tYou can append 'verbose' on the command line in order to enable verbose output.\n"
            + "\t\tExample: java -jar space-invaders-*.jar client verbose\n\n";

    if (args.length == 0) {
      System.out.println(help);
      return;
    }

    Boolean verbose = false;
    for (String arg : args) {
      if (arg.equals("verbose")) {
        verbose = true;
        break;
      }
    }
    if (verbose) {
      setGlobalLoggingLevel(INFO);
    } else {
      setGlobalLoggingLevel(SEVERE);
    }

    switch (args[0]) {
      case "help":
        System.out.println(help);
        break;
      case "client":
        Client client = new Client();
        client.call();
        break;
      case "server":
        try {
          if (args.length < 2) {
            System.out.println(help);
            return;
          }
          Server server = new Server(Integer.parseInt(args[1]));
          ServiceController controller = new StandardController(server);
          ExecutorService controllerExecutor = Executors.newSingleThreadExecutor();
          Future<Void> controllerFuture = controllerExecutor.submit(controller);
          System.out.println("Server started.\nEnter 'quit' to stop.\n");
          server.call();
          controllerFuture.get();
          controllerExecutor.shutdownNow();
        } catch (Exception exception) {
          LOGGER.log(SEVERE,exception.toString(),exception);
        }
        break;
      default:
        System.out.println(help);
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
