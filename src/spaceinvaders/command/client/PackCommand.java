package spaceinvaders.command.client;

import static spaceinvaders.command.ProtocolEnum.UDP;

import java.util.List;
import spaceinvaders.client.mvc.Controller;
import spaceinvaders.client.mvc.View;
import spaceinvaders.command.Command;

/**
 * Multiple commands in one command.
 *
 * <p>Used for networking efficiency.
 */
public class PackCommand extends Command {
  private transient Controller executor;
  private List<Command> pack;

  PackCommand() {
    super(PackCommand.class.getName(),UDP);
  }

  public PackCommand(List<Command> pack) {
    this();
    this.pack = pack;
  }

  @Override
  public void execute() {
    //executor.getModel().exitGame();
  }

  @Override
  public void setExecutor(Object executor) {
    if (executor instanceof Controller) {
      this.executor = (Controller) executor;
    }
  }
}

