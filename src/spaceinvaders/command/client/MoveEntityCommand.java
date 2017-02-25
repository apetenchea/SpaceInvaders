package spaceinvaders.command.client;

import static spaceinvaders.command.ProtocolEnum.UDP;

import spaceinvaders.client.mvc.Controller;
import spaceinvaders.client.mvc.View;
import spaceinvaders.command.Command;

/** Move an entity to a new position. */
public class MoveEntityCommand extends Command {
  private transient Controller executor;
  private Integer entityId;
  private Integer newX;
  private Integer newY;

  MoveEntityCommand() {
    super(MoveEntityCommand.class.getName(),UDP);
  }

  /**
   * @param entityId id of the entity to be moved.
   * @param newX new x-axis coordinate.
   * @param newY new y-axis coordinate.
   */
  public MoveEntityCommand(int entityId, int newX, int newY) {
    this();
    this.entityId = entityId;
    this.newX = newX;
    this.newY = newY;
  }

  @Override
  public void execute() {
    for (View view : executor.getViews()) {
      view.moveEntity(entityId,newX,newY);
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
