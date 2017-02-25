package spaceinvaders.command.client;

import static spaceinvaders.command.ProtocolEnum.TCP;

import spaceinvaders.client.mvc.Controller;
import spaceinvaders.client.mvc.View;
import spaceinvaders.command.Command;

/** Wipe out a destroyed entity from the screen. */
public class WipeOutEntityCommand extends Command {
  private transient Controller executor;
  private int entityId;

  WipeOutEntityCommand() {
    super(WipeOutEntityCommand.class.getName(),TCP);
  }

  /**
   * @param entityId the id of the entity to be removed.
   */
  public WipeOutEntityCommand(int entityId) {
    this();
    this.entityId = entityId;
  }

  @Override
  public void execute() {
    for (View view : executor.getViews()) {
      view.wipeOutEntity(entityId);
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
