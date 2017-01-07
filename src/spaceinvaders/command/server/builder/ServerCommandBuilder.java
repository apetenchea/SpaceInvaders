package spaceinvaders.command.server.builder;

import spaceinvaders.command.CommandBuilder;
import spaceinvaders.command.server.ConfigurePlayerCommand;

/**
 * Builds commands for the server.
 */
public class ServerCommandBuilder extends CommandBuilder {

  /**
   * Construct the builder with all the available commands.
   */
  public ServerCommandBuilder() {
    super(new ConfigurePlayerCommand());
  }
}
