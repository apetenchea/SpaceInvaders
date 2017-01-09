package spaceinvaders.command.client;

import java.util.List;
import spaceinvaders.client.mvc.Controller;
import spaceinvaders.client.mvc.View;
import spaceinvaders.command.Command;
import spaceinvaders.utility.Couple;

/**
 * The game is over.
 */
public class GameOverCommand extends Command {
  private transient Controller executor;

  public GameOverCommand() {
    super(GameOverCommand.class.getName());
  }

  @Override
  public void execute() {
    executor.getModel().exitGame();
  }

  @Override
  public void setExecutor(Object executor) {
    if (executor instanceof Controller) {
      this.executor = (Controller) executor;
    }
  }

}

