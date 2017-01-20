package spaceinvaders.command.client;

import static spaceinvaders.command.ProtocolEnum.UDP;

import java.util.List;

import spaceinvaders.client.mvc.Controller;
import spaceinvaders.client.mvc.View;
import spaceinvaders.command.Command;
import spaceinvaders.game.Entity;

/** Refresh entities that appear on the screen. */
public class RefreshEntitiesCommand extends Command {
  private transient Controller executor;
  private List<Entity> entities;

  public RefreshEntitiesCommand() {
    super(RefreshEntitiesCommand.class.getName(),UDP);
  }

  public RefreshEntitiesCommand(List<Entity> entities) {
    this();
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
      throw new AssertionError();
    }
  }
}
