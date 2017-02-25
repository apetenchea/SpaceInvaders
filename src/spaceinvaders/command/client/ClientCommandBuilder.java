package spaceinvaders.command.client;

import spaceinvaders.command.CommandBuilder;

/** Builds commands wich are going to be executed on the client side. */
public class ClientCommandBuilder extends CommandBuilder {
  /** Construct the builder with all the available commands. */
  public ClientCommandBuilder() {
    super(new FlushScreenCommand(),
          new GameOverCommand(),
          new ChangeScoreCommand(),
          new MoveEntityCommand(),
          new PlayersWonCommand(),
          new PlayersLostCommand(),
          new RefreshEntitiesCommand(),
          new SetPlayerIdCommand(),
          new SetPlayerNamesCommand(),
          new SpawnEntityCommand(),
          new StartGameCommand(),
          new TranslateGroupCommand(),
          new WipeOutEntityCommand());
  }
}
