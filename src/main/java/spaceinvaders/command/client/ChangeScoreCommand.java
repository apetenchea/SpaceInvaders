package spaceinvaders.command.client;

import static spaceinvaders.command.ProtocolEnum.UDP;

import spaceinvaders.client.mvc.Controller;
import spaceinvaders.client.mvc.View;
import spaceinvaders.command.Command;

/** Change the score of a player. */
public class ChangeScoreCommand extends Command {
  private transient Controller executor;
  private Integer playerId;
  private Integer change;

  ChangeScoreCommand() {
    super(ChangeScoreCommand.class.getName(),UDP);
  }

  /**
   * @param playerId the id of the player.
   * @param change a value with which the score is changed.
   */
  public ChangeScoreCommand(int playerId, int change) {
    this();
    this.playerId = playerId;
    this.change = change;
  }

  @Override
  public void execute() {
    for (View view : executor.getViews()) {
      view.changeScore(playerId,change);
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
