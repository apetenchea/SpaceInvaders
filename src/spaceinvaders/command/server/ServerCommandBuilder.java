package spaceinvaders.command.server;

import spaceinvaders.command.CommandBuilder;

/** Builds commands for the server. */
public class ServerCommandBuilder extends CommandBuilder {
  /** Construct the builder with all the available commands. */
  public ServerCommandBuilder() {
    super(new ConfigurePlayerCommand(),
          new MovePlayerLeftCommand(),
          new MovePlayerRightCommand(),
          new PlayerShootCommand());
  }
}
