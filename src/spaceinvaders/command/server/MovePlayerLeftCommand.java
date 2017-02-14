package spaceinvaders.command.server;

import static spaceinvaders.command.ProtocolEnum.UDP;

import spaceinvaders.command.Command;
import spaceinvaders.server.game.GameLoop;

/** Move a player to the left. */
public class MovePlayerLeftCommand extends Command {
  private transient GameLoop executor;
  private Integer id;

  public MovePlayerLeftCommand() {
    super(MovePlayerLeftCommand.class.getName(),UDP);
  }

  public MovePlayerLeftCommand(int id) {
    this();
    this.id = id;
  }

  @Override
  public void execute() {
    executor.movePlayerLeft(id);
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
