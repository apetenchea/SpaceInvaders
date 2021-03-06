package spaceinvaders.command.client;

import static spaceinvaders.command.ProtocolEnum.TCP;

import spaceinvaders.client.ClientConfig;
import spaceinvaders.client.mvc.Controller;
import spaceinvaders.client.mvc.Model;
import spaceinvaders.client.mvc.View;
import spaceinvaders.command.Command;
import spaceinvaders.command.server.ConfigurePlayerCommand;

/** Set the id of the player, which is generated by the server. */
public class SetPlayerIdCommand extends Command {
  private transient Controller executor;
  private Integer id;

  SetPlayerIdCommand() {
    super(SetPlayerIdCommand.class.getName(),TCP);
  }

  /**
   * @param id the id of the player.
   */
  public SetPlayerIdCommand(int id) {
    this();
    this.id = id;
  }

  @Override
  public void execute() {
    ClientConfig config = ClientConfig.getInstance();
    config.setId(id);
    Model model = executor.getModel();
    model.doCommand(new ConfigurePlayerCommand(config.getUserName(),config.getTeamSize(),
          config.getUdpIncomingPort()));
    for (View view : executor.getViews()) {
      view.showGame();
    }
  }

  @Override
  public void setExecutor(Object executor) {
    if (executor instanceof Controller) {
      this.executor = (Controller) executor;
    } else {
      // This should never happen.
      throw new AssertionError();
    }
  }
}
