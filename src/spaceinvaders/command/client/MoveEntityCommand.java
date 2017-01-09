package spaceinvaders.command.client;

import spaceinvaders.client.mvc.Controller;
import spaceinvaders.client.mvc.View;
import spaceinvaders.command.Command;

/**
 * Reset the position of a game entity.
 */
public class MoveEntityCommand extends Command {
  private transient Controller executor;
  private Integer id;
  private Integer newX;
  private Integer newY;

  public MoveEntityCommand() {
    super(MoveEntityCommand.class.getName());
  }

  public MoveEntityCommand(int id, int newX, int newY) {
    this();
    this.id = id;
    this.newX = newX;
    this.newY = newY;
  }

  @Override
  public void execute() {
    for (View view : executor.getViews()) {
      view.moveEntity(id,newX,newY);
    }
  }

  @Override
  public void setExecutor(Object executor) {
    if (executor instanceof Controller) {
      this.executor = (Controller) executor;
    }
  }

}

