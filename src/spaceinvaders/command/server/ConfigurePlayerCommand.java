package spaceinvaders.command.server;

import spaceinvaders.command.Command;
import spaceinvaders.server.players.Player;

/**
 * Set the name and team size preferences for the player.
 */
public class ConfigurePlayerCommand extends Command {
  private transient Player executor;
  private String name;
  private Integer teamSize;

  public ConfigurePlayerCommand() {
    super(ConfigurePlayerCommand.class.getName());
  }

  public ConfigurePlayerCommand(String name, int teamSize) {
    this();
    this.name = name;
    this.teamSize = teamSize;
  }

  @Override
  public void execute() {
    executor.setName(name);
    executor.setTeamSize(teamSize);
  }

  public void setExecutor(Player executor) {
    this.executor = executor;
  }
} 
