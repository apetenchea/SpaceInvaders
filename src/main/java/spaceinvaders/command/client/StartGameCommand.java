package spaceinvaders.command.client;

import static spaceinvaders.command.ProtocolEnum.TCP;

import spaceinvaders.client.mvc.Controller;
import spaceinvaders.client.mvc.View;
import spaceinvaders.command.Command;

/** Start the game. */
public class StartGameCommand extends Command {
  private transient Controller executor;

  public StartGameCommand() {
    super(StartGameCommand.class.getName(),TCP);
  }

  @Override
  public void execute() {
    for (View view : executor.getViews()) {
      view.startGame();
    }
    executor.getModel().setGameState(true);
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
