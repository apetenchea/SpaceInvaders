package spaceinvaders.command.client;

import static spaceinvaders.command.ProtocolEnum.TCP;

import spaceinvaders.client.mvc.Controller;
import spaceinvaders.command.Command;

/** Quit game. */
public class QuitGameCommand extends Command {
  private transient Controller executor;

  public QuitGameCommand() {
    super(QuitGameCommand.class.getName(),TCP);
  }

  @Override
  public void execute() {
    executor.getModel().exitGame();
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
