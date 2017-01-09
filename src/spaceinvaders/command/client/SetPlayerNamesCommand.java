package spaceinvaders.command.client;

import java.util.List;
import spaceinvaders.client.mvc.Controller;
import spaceinvaders.client.mvc.View;
import spaceinvaders.command.Command;
import spaceinvaders.utility.Couple;

/**
 * Associate player names with their IDs.
 */
public class SetPlayerNamesCommand extends Command {
  private transient Controller executor;
  private List<Couple<Integer,String>> list;

  public SetPlayerNamesCommand() {
    super(SetPlayerNamesCommand.class.getName());
  }
  public SetPlayerNamesCommand(List<Couple<Integer,String>> list) {
    this();
    this.list = list;
  }

  @Override
  public void execute() {
    for (View view : executor.getViews()) {
      view.setPlayerNames(list);
    }
  }

  @Override
  public void setExecutor(Object executor) {
    if (executor instanceof Controller) {
      this.executor = (Controller) executor;
    }
  }

}

