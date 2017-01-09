package spaceinvaders.command.server.builder;

import spaceinvaders.command.CommandBuilder;
import spaceinvaders.command.server.ConfigurePlayerCommand;
import spaceinvaders.command.server.MovePlayerLeftCommand;
import spaceinvaders.command.server.MovePlayerRightCommand;
import spaceinvaders.command.server.PlayerShootCommand;

/**
 * Builds commands for the server.
 */
public class ServerCommandBuilder extends CommandBuilder {

  /**
   * Construct the builder with all the available commands.
   */
  public ServerCommandBuilder() {
    super(new ConfigurePlayerCommand(),
        new MovePlayerLeftCommand(),
        new MovePlayerRightCommand(),
        new PlayerShootCommand());
  }
}
