package spaceinvaders.command.client;

import spaceinvaders.client.mvc.Controller;
import spaceinvaders.command.Command;

/**
 * Flush all readed data on the screen.
 */
public class FlushScreenCommand extends Command {
  private transient Controller executor;

  public FlushScreenCommand() {
    super(FlushScreenCommand.class.getName());
  }

  @Override
  public void execute() {
    executor.flushViews();
  }

  @Override
  public void setExecutor(Object executor) {
    if (executor instanceof Controller) {
      this.executor = (Controller) executor;
    }
  }

}
