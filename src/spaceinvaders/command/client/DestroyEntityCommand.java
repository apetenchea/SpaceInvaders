package spaceinvaders.command.client;

import spaceinvaders.client.mvc.Controller;
import spaceinvaders.client.mvc.View;
import spaceinvaders.command.Command;

/**
 * Remove an entity from the game.
 */
public class DestroyEntityCommand extends Command {
  private transient Controller executor;
  private Integer id;

  public DestroyEntityCommand() {
    super(DestroyEntityCommand.class.getName());
  }

  public DestroyEntityCommand(int id) {
    this();
    this.id = id;
  }

  @Override
  public void execute() {
    for (View view : executor.getViews()) {
      view.destroyEntity(id);
    }
  }

  @Override
  public void setExecutor(Object executor) {
    if (executor instanceof Controller) {
      this.executor = (Controller) executor;
    }
  }

}

