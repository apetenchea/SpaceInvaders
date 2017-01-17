package spaceinvaders.command.server;

import java.net.SocketAddress;
import spaceinvaders.command.Command;
import spaceinvaders.server.player.Player;

/**
 * Set the name, team size and the address where to send UDP packets.
 */
public class ConfigurePlayerCommand extends Command {
  private transient Player executor;
  private final String playerName;
  private final Integer teamSize;
  private final SocketAddress address;

  public ConfigurePlayerCommand() {
    super(ConfigurePlayerCommand.class.getName());
  }

  public ConfigurePlayerCommand(String playerName, int teamSize, SocketAddress address) {
    this();
    this.playerName = playerName;
    this.teamSize = teamSize;
    this.address = address;
  }

  @Override
  public void execute() {
    executor.setName(playerName);
    executor.setTeamSize(teamSize);
    executor.setUdpDestination(address);
  }

  @Override
  public void setExecutor(Object executor) {
    if (executor instanceof Player) {
      this.executor = (Player) executor;
    }
  }
} 
