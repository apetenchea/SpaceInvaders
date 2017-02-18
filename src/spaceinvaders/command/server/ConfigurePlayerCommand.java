package spaceinvaders.command.server;

import static spaceinvaders.command.ProtocolEnum.TCP;

import spaceinvaders.command.Command;
import spaceinvaders.server.player.Player;

/** Set the name, team size and the address where to send UDP packets. */
public class ConfigurePlayerCommand extends Command {
  private transient Player executor;
  private String playerName;
  private Integer teamSize;
  private Integer receivingUdpPort;

  ConfigurePlayerCommand() {
    super(ConfigurePlayerCommand.class.getName(),TCP);
  }

  public ConfigurePlayerCommand(String playerName, int teamSize, int receivingUdpPort) {
    this();
    this.playerName = playerName;
    this.teamSize = teamSize;
    this.receivingUdpPort = receivingUdpPort;
  }

  @Override
  public void execute() {
    executor.setName(playerName);
    executor.setTeamSize(teamSize);
    executor.setUdpDestinationPort(receivingUdpPort);
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
