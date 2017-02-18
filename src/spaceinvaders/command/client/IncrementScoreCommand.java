package spaceinvaders.command.client;

import static spaceinvaders.command.ProtocolEnum.UDP;

import spaceinvaders.client.mvc.Controller;
import spaceinvaders.client.mvc.View;
import spaceinvaders.command.Command;

/** Increment the score of the player. */
public class IncrementScoreCommand extends Command {
  private transient Controller executor;

  public IncrementScoreCommand() {
    super(IncrementScoreCommand.class.getName(),UDP);
  }

  @Override
  public void execute() {
    for (View view : executor.getViews()) {
      view.incrementScore();
    }
  }

  @Override
  public void setExecutor(Object executor) {
    if (executor instanceof Controller) {
      this.executor = (Controller) executor;
    } else {
      throw new AssertionError();
    }
  }
}
