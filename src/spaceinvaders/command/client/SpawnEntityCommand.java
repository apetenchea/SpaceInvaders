package spaceinvaders.command.client;

import static spaceinvaders.command.ProtocolEnum.TCP;

import spaceinvaders.client.mvc.Controller;
import spaceinvaders.client.mvc.View;
import spaceinvaders.command.Command;
import spaceinvaders.game.EntityEnum;

/** Create a new entity. */
public class SpawnEntityCommand extends Command {
  private transient Controller executor;
  private Integer id;
  private EntityEnum type;
  private Integer posX;
  private Integer posY;

  SpawnEntityCommand() {
    super(SpawnEntityCommand.class.getName(),TCP);
  }

  /**
   * @param id entity id.
   * @param type entity type.
   * @param posX x-axis coordinate.
   * @param posY y-axis coordinate.
   *
   * @throws NullPointerException - if an argument is {@code null}.
   */
  public SpawnEntityCommand(int id, EntityEnum type, int posX, int posY) {
    this();
    this.id = id;
    this.type = type;
    this.posX = posX;
    this.posY = posY;
  }

  @Override
  public void execute() {
    for (View view : executor.getViews()) {
      view.spawnEntity(id,type,posX,posY);
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
