package spaceinvaders.command.server;

import spaceinvaders.command.Command;
import spaceinvaders.server.players.Player;

/**
 * Set the name and team size preferences for the player.
 */
public class ConfigurePlayerCommand extends Command {
  private transient Player executor;
  private String playerName;
  private Integer teamSize;

  public ConfigurePlayerCommand() {
    super(ConfigurePlayerCommand.class.getName());
  }

  public ConfigurePlayerCommand(String playerName, int teamSize) {
    this();
    this.playerName = playerName;
    this.teamSize = teamSize;
  }

  @Override
  public void execute() {
    executor.setName(playerName);
    executor.setTeamSize(teamSize);
  }

  @Override
  public void setExecutor(Object executor) {
    if (executor instanceof Object) {
      this.executor = (Player) executor;
    }
  }
} 
