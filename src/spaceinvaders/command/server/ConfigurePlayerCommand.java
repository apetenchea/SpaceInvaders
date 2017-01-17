package spaceinvaders.command.server;

import static spaceinvaders.command.ProtocolEnum.TCP;

import java.net.SocketAddress;
import spaceinvaders.command.Command;
import spaceinvaders.server.player.Player;

/** Set the name, team size and the address where to send UDP packets. */
public class ConfigurePlayerCommand extends Command {
  private transient Player executor;
  private String playerName;
  private Integer teamSize;
  private SocketAddress address;

  public ConfigurePlayerCommand() {
    super(ConfigurePlayerCommand.class.getName(),TCP);
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
    } else {
      throw new AssertionError();
    }
  }
} 
