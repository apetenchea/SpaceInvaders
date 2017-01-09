package spaceinvaders.command.client.builder;

import spaceinvaders.command.CommandBuilder;
import spaceinvaders.command.client.AddEntityCommand;
import spaceinvaders.command.client.SetPlayerIdCommand;
import spaceinvaders.command.client.SetPlayerNamesCommand;
import spaceinvaders.command.client.FlushScreenCommand;
import spaceinvaders.command.client.MoveEntityCommand;
import spaceinvaders.command.client.GameOverCommand;
import spaceinvaders.command.client.DestroyEntityCommand;

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
