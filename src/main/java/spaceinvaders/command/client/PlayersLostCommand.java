package spaceinvaders.command.client;

import static spaceinvaders.command.ProtocolEnum.TCP;

import spaceinvaders.client.mvc.Controller;
import spaceinvaders.client.mvc.View;
import spaceinvaders.command.Command;

/** Humans lost the game. */
public class PlayersLostCommand extends Command {
  private transient Controller executor;

  public PlayersLostCommand() {
    super(PlayersLostCommand.class.getName(),TCP);
  }

  @Override
  public void execute() {
    for (View view : executor.getViews()) {
      view.youLost();
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
