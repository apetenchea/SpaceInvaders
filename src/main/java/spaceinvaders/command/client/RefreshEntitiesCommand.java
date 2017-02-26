package spaceinvaders.command.client;

import static spaceinvaders.command.ProtocolEnum.TCP;

import java.util.List;

import spaceinvaders.client.mvc.Controller;
import spaceinvaders.client.mvc.View;
import spaceinvaders.command.Command;
import spaceinvaders.game.Entity;

/** Refresh entities that appear on the screen. */
public class RefreshEntitiesCommand extends Command {
  private transient Controller executor;
  private List<Entity> entities;

  RefreshEntitiesCommand() {
    super(RefreshEntitiesCommand.class.getName(),TCP);
  }

  /**
   * @param entities list containing the entities which should remain after the refresh.
   *
   * @throws NullPointerException if argument is {@code null}.
   */
  public RefreshEntitiesCommand(List<Entity> entities) {
    this();
    if (entities == null) {
      throw new NullPointerException();
    }
    this.entities = entities;
  }

  @Override
  public void execute() {
    for (View view : executor.getViews()) {
      view.setFrameContent(entities);
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
