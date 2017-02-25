package spaceinvaders.command.client;

import static spaceinvaders.command.ProtocolEnum.TCP;

import spaceinvaders.client.mvc.Controller;
import spaceinvaders.client.mvc.View;
import spaceinvaders.command.Command;

/** Humans won the game. */
public class PlayersWonCommand extends Command {
  private transient Controller executor;

  public PlayersWonCommand() {
    super(PlayersWonCommand.class.getName(),TCP);
  }

  @Override
  public void execute() {
    for (View view : executor.getViews()) {
      view.youWon();
    }
    executor.getModel().setGameState(false);
  }

  @Override
  public void setExecutor(Object executor) {
    if (executor instanceof Controller) {
      this.executor = (Controller) executor;
    } else {
      // This should never happen.
      throw new AssertionError();
    }
  }
}
