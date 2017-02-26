package spaceinvaders.command.server;

import static spaceinvaders.command.ProtocolEnum.UDP;

import spaceinvaders.command.Command;
import spaceinvaders.server.game.GameLoop;

/** Player fires a bullet. */
public class PlayerShootCommand extends Command {
  private transient GameLoop executor;
  private Integer id;

  PlayerShootCommand() {
    super(PlayerShootCommand.class.getName(),UDP);
  }

  /**
   * @param id player id.
   */
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
    if (executor instanceof GameLoop) {
      this.executor = (GameLoop) executor;
    } else {
      // This should never happen.
      throw new AssertionError();
    }
  }
} 


