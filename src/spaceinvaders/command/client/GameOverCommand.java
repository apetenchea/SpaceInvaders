package spaceinvaders.command.client;

import static spaceinvaders.command.ProtocolEnum.UDP;

import spaceinvaders.client.mvc.Controller;
import spaceinvaders.client.mvc.View;
import spaceinvaders.command.Command;

/** The player dies. */
public class GameOverCommand extends Command {
  private transient Controller executor;

  public GameOverCommand() {
    super(GameOverCommand.class.getName(),UDP);
  }

  @Override
  public void execute() {
    //executor.getModel().exitGame();
  }

  @Override
  public void setExecutor(Object executor) {
    if (executor instanceof Controller) {
      this.executor = (Controller) executor;
    }
  }

}

