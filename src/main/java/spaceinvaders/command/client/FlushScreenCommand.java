package spaceinvaders.command.client;

import static spaceinvaders.command.ProtocolEnum.UDP;

import spaceinvaders.client.mvc.Controller;
import spaceinvaders.client.mvc.View;
import spaceinvaders.command.Command;

/** Flush the screen. */
public class FlushScreenCommand extends Command {
  private transient Controller executor;

  public FlushScreenCommand() {
    super(FlushScreenCommand.class.getName(),UDP);
  }

  @Override
  public void execute() {
    for (View view : executor.getViews()) {
      view.flush();
    }
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
