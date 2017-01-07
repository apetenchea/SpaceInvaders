package spaceinvaders.command.client;

import spaceinvaders.client.mvc.View;
import spaceinvaders.command.Command;
import spaceinvaders.game.GameWorld;

/**
 * Describe the game world.
 */
public class InitGameWorldCommand extends Command {
  private transient View executor;
  private GameWorld world;

  public InitGameWorldCommand() {
    super(InitGameWorldCommand.class.getName());
  }

  public InitGameWorldCommand(GameWorld world) {
    this();
    this.world = world;
  }

  @Override
  public void execute() {
    executor.initGameWorld(world);
  }

  @Override
  public void setExecutor(Object executor) {
    if (executor instanceof View) {
      this.executor = (View) executor;
    }
  }

}
