package spaceinvaders.command.server;

import spaceinvaders.command.Command;
import spaceinvaders.server.game.Game;

/**
 * Move a player to the left.
 */
public class MovePlayerLeftCommand extends Command {
  private transient Game executor;
  private Integer id;

  public MovePlayerLeftCommand() {
    super(MovePlayerLeftCommand.class.getName());
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
    if (executor instanceof Game) {
      this.executor = (Game) executor;
    }
  }
} 
