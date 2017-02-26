package spaceinvaders.command.client;

import static spaceinvaders.command.ProtocolEnum.TCP;

import spaceinvaders.client.ClientConfig;
import spaceinvaders.client.mvc.Controller;
import spaceinvaders.client.mvc.View;
import spaceinvaders.command.Command;

/** The player dies. */
public class GameOverCommand extends Command {
  private transient Controller executor;
  private Integer playerId;

  GameOverCommand() {
    super(GameOverCommand.class.getName(),TCP);
  }

  public GameOverCommand(int playerId) {
    this();
    this.playerId = playerId;
  }

  @Override
  public void execute() {
    if (ClientConfig.getInstance().getId() == playerId) {
      for (View view : executor.getViews()) {
        view.gameOver();
      }
      executor.getModel().setGameState(false);
    }
  }

  @Override
  public void setExecutor(Object executor) {
    if (executor instanceof Controller) {
      this.executor = (Controller) executor;
    }
  }
}

