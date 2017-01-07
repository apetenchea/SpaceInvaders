package spaceinvaders.command.client;

import spaceinvaders.client.mvc.Controller;
import spaceinvaders.command.Command;

/**
 * Set the ID of the player.
 */
public class SetPlayerIdCommand extends Command {
  private transient Controller executor;
  private Integer id;

  public SetPlayerIdCommand() {
    super(SetPlayerIdCommand.class.getName());
  }
  public SetPlayerIdCommand(int id) {
    this();
    this.id = id;
  }

  @Override
  public void execute() {
    executor.configurePlayer(id);
  }

  @Override
  public void setExecutor(Object executor) {
    if (executor instanceof Controller) {
      this.executor = (Controller) executor;
    }
  }

}
