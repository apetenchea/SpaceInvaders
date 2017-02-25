package spaceinvaders.command.server;

import spaceinvaders.command.CommandBuilder;

/** Builds commands which are executed on the server side. */
public class ServerCommandBuilder extends CommandBuilder {
  /** Construct the builder with all the available commands. */
  public ServerCommandBuilder() {
    super(new ConfigurePlayerCommand(),
          new MovePlayerLeftCommand(),
          new MovePlayerRightCommand(),
          new PlayerShootCommand());
  }
}
