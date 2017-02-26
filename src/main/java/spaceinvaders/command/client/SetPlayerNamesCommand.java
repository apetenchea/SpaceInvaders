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

  SetPlayerNamesCommand() {
    super(SetPlayerNamesCommand.class.getName(),TCP);
  }

  /**
   * @param idToName a list containing 2-tuples of the form {player id, player name}.
   *
   * @throws NullPointerException if argument is {@code null}.
   */
  public SetPlayerNamesCommand(List<Couple<Integer,String>> idToName) {
    this();
    if (idToName == null) {
      throw new NullPointerException();
    }
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
      // This should never happen.
      throw new AssertionError();
    }
  }
}
