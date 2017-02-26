package spaceinvaders.command.client;

import static spaceinvaders.command.ProtocolEnum.UDP;

import spaceinvaders.client.mvc.Controller;
import spaceinvaders.client.mvc.View;
import spaceinvaders.command.Command;
import spaceinvaders.game.EntityEnum;

/** Move a all entities belonging to a groupe, relative to their current position. */
public class TranslateGroupCommand extends Command {
  private transient Controller executor;
  private EntityEnum type;
  private Integer offsetX;
  private Integer offsetY;

  TranslateGroupCommand() {
    super(TranslateGroupCommand.class.getName(),UDP);
  }

  /**
   * @param type entities type.
   * @param offsetX offset on the x-axis.
   * @param offsetY offset on the y-axis.
   *
   * @throws NullPointerException if an argument is {@code null}.
   */
  public TranslateGroupCommand(EntityEnum type, int offsetX, int offsetY) {
    this();
    if (type == null) {
      throw new NullPointerException();
    }
    this.type = type;
    this.offsetX = offsetX;
    this.offsetY = offsetY;
  }

  @Override
  public void execute() {
    for (View view : executor.getViews()) {
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
