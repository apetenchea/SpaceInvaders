package spaceinvaders.command.client;

import spaceinvaders.command.CommandBuilder;

/**
 * Builds commands for the client.
 */
public class ClientCommandBuilder extends CommandBuilder {

  /**
   * Construct the builder with all the available commands.
   */
  public ClientCommandBuilder() {
    super(new SetPlayerIdCommand(),
        new AddEntityCommand(),
        new SetPlayerNamesCommand(),
        new FlushScreenCommand(),
        new MoveEntityCommand(),
        new GameOverCommand(),
        new DestroyEntityCommand());
  }
}
