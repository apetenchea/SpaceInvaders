package spaceinvaders.command.client;

import java.util.List;
import spaceinvaders.client.mvc.View;
import spaceinvaders.command.Command;
import spaceinvaders.utility.Couple;

/**
 * Provide the names of the players.
 */
public class InitPlayersCommand extends Command {
  private transient View executor;
  private List<Couple<Integer,String>> players;

  public InitPlayersCommand() {
    super(InitPlayersCommand.class.getName());
  }

  public InitPlayersCommand(List<Couple<Integer,String>> players) {
    this();
    this.players = players;
  }

  @Override
  public void execute() {
    executor.setPlayers(players);
  }

  @Override
  public void setExecutor(Object executor) {
    if (executor instanceof View) {
      this.executor = (View) executor;
    }
  }
}
