package spaceinvaders.command.server;

import spaceinvaders.command.Command;
import spaceinvaders.server.game.Game;

/**
 * Player fires a bullet.
 */
public class PlayerShootCommand extends Command {
  private transient Game executor;
  private Integer id;

  public PlayerShootCommand() {
    super(PlayerShootCommand.class.getName());
  }

  public PlayerShootCommand(int id) {
    this();
    this.id = id;
  }

  @Override
  public void execute() {
    executor.playerShoots(id);
  }

  @Override
  public void setExecutor(Object executor) {
    if (executor instanceof Game) {
      this.executor = (Game) executor;
    }
  }
} 


