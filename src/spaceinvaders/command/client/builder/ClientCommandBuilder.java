package spaceinvaders.command.client.builder;

import spaceinvaders.command.CommandBuilder;
import spaceinvaders.command.client.InitGameWorldCommand;
import spaceinvaders.command.client.InitPlayersCommand;
import spaceinvaders.command.client.SetPlayerIdCommand;

/**
 * Builds commands for the client.
 */
public class ClientCommandBuilder extends CommandBuilder {

  /**
   * Construct the builder with all the available commands.
   */
  public ClientCommandBuilder() {
    super(new SetPlayerIdCommand(),
        new InitGameWorldCommand(),
        new InitPlayersCommand());
  }
}
