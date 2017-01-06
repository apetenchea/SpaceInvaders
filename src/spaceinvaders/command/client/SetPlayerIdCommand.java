package spaceinvaders.command.client;

import spaceinvaders.client.mvc.Model;
import spaceinvaders.command.Command;

/**
 * Set the ID of the player.
 */
public class SetPlayerIdCommand extends Command {
  private transient Model executor;
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
    executor.setPlayerId(id);
    executor.startSendingPackets();
  }

  public void setExecutor(Model executor) {
    this.executor = executor;
  }
}
