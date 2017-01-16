package spaceinvaders.command.client;

import spaceinvaders.client.ClientConfig;
import spaceinvaders.client.mvc.Controller;
import spaceinvaders.client.mvc.Model;
import spaceinvaders.client.mvc.View;
import spaceinvaders.command.Command;
import spaceinvaders.command.server.ConfigurePlayerCommand;

/**
 * Set the ID of the player.
 */
public class SetPlayerIdCommand extends Command {
  private transient Controller executor;
  public Integer id;

  public SetPlayerIdCommand() {
    super(SetPlayerIdCommand.class.getName());
  }
  public SetPlayerIdCommand(int id) {
    this();
    this.id = id;
  }

  @Override
  public void execute() {
    ClientConfig config = ClientConfig.getInstance();
    config.setId(id);
    //executor.getModel().startSendingPackets();
    executor.getModel().doCommand(
        new ConfigurePlayerCommand(config.getUserName(),config.getTeamSize()));
    for (View view : executor.getViews()) {
      view.showGame();
    }
  }

  @Override
  public void setExecutor(Object executor) {
    if (executor instanceof Controller) {
      this.executor = (Controller) executor;
    }
  }

}
