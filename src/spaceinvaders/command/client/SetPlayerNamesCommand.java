package spaceinvaders.command.client;

import static spaceinvaders.command.ProtocolEnum.TCP;

import java.util.List;
import spaceinvaders.client.mvc.Controller;
import spaceinvaders.client.mvc.View;
import spaceinvaders.command.Command;
import spaceinvaders.utility.Couple;

/** Associate players ids with names. */
public class SetPlayerNamesCommand extends Command {
  private transient Controller executor;
  private List<Couple<Integer,String>> idToName;

  public SetPlayerNamesCommand() {
    super(SetPlayerNamesCommand.class.getName(),TCP);
  }

  public SetPlayerNamesCommand(List<Couple<Integer,String>> idToName) {
    this();
    this.idToName = idToName;
  }

  @Override
  public void execute() {
    for (View view : executor.getViews()) {
      view.setPlayerNames(idToName);
    }
  }

  @Override
  public void setExecutor(Object executor) {
    if (executor instanceof Controller) {
      this.executor = (Controller) executor;
    } else {
      throw new AssertionError();
    }
  }
}
