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

  /**
   * @param playerName the name which the player chooses to have.
   * @param teamSize the size of the time which the player wants to join.
   * @param receivingUdpPort the UDP port on which the player is going to listen.
   *
   * @throws NullPointerException if an argument is {@code null}.
   */
  public ConfigurePlayerCommand(String playerName, int teamSize, int receivingUdpPort) {
    this();
    if (playerName == null) {
      throw new NullPointerException();
    }
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
      // This should never happen.
      throw new AssertionError();
    }
  }
} 
