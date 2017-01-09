package spaceinvaders.command.server;

import spaceinvaders.command.Command;
import spaceinvaders.server.game.Game;

/**
 * Move a player to the right.
 */
public class MovePlayerRightCommand extends Command {
  private transient Game executor;
  private Integer id;

  public MovePlayerRightCommand() {
    super(MovePlayerRightCommand.class.getName());
  }

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
    if (executor instanceof Game) {
      this.executor = (Game) executor;
    }
  }
} 

