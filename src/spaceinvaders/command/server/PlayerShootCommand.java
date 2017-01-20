package spaceinvaders.command.server;

import static spaceinvaders.command.ProtocolEnum.UDP;

import spaceinvaders.command.Command;
import spaceinvaders.server.game.Game;

/** Player fires a bullet. */
public class PlayerShootCommand extends Command {
  private transient Game executor;
  private Integer id;

  public PlayerShootCommand() {
    super(PlayerShootCommand.class.getName(),UDP);
  }

  public PlayerShootCommand(int id) {
    this();
    this.id = id;
  }

  @Override
  public void execute() {
    executor.playerShoot(id);
  }

  @Override
  public void setExecutor(Object executor) {
    if (executor instanceof Game) {
      this.executor = (Game) executor;
    } else {
      throw new AssertionError();
    }
  }
} 


