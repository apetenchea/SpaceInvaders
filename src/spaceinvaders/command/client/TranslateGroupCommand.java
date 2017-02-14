package spaceinvaders.command.client;

import static spaceinvaders.command.ProtocolEnum.UDP;

import spaceinvaders.client.mvc.Controller;
import spaceinvaders.client.mvc.View;
import spaceinvaders.command.Command;
import spaceinvaders.game.EntityEnum;

/** Move a group of entities relative to their position. */
public class TranslateGroupCommand extends Command {
  private transient Controller executor;
  private EntityEnum type;
  private Integer offsetX;
  private Integer offsetY;

  TranslateGroupCommand() {
    super(TranslateGroupCommand.class.getName(),UDP);
  }

  public TranslateGroupCommand(EntityEnum type, int offsetX, int offsetY) {
    this();
    this.type = type;
    this.offsetX = offsetX;
    this.offsetY = offsetY;
  }

  @Override
  public void execute() {
    for (View voew : executor.getViews()) {
      view.translateGroup(type,offsetX,offsetY);
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
