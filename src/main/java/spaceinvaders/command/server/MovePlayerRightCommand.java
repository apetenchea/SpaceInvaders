package spaceinvaders.command.server;

import static spaceinvaders.command.ProtocolEnum.UDP;

import spaceinvaders.command.Command;
import spaceinvaders.server.game.GameLoop;

/** Move a player one step to the right. */
public class MovePlayerRightCommand extends Command {
  private transient GameLoop executor;
  private Integer id;

  MovePlayerRightCommand() {
    super(MovePlayerRightCommand.class.getName(),UDP);
  }

  /**
   * @param id player id.
   */
  public MovePlayerRightCommand(int id) {
    this();
    this.id = id;
  }

  @Override
  public void execute() {
    executor.movePlayerRight(id);
  }

  @Override
  public void setExecutor(Object executor) {
    if (executor instanceof GameLoop) {
      this.executor = (GameLoop) executor;
    } else {
      // This should never happen.
      throw new AssertionError();
    }
  }
} 

