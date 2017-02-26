package spaceinvaders.command;

import com.google.gson.Gson;
import spaceinvaders.Config;

/**
 * Command to be executed on the client or the server side.
 *
 * <p>Commands are sent over the network, and are serialized using the json format.
 */
public abstract class Command {
  private static final transient Gson GSON = new Gson();

  private final transient ProtocolEnum protocol;
  private final String name;


  /**
   * {@strong Note:} In case the game in not played in LAN, only TCP will be used.
   *
   * @param name the name of the command, used for identification (usually the name of the class).
   * @param protocol specifies the internet protocol used to send the command.
   */
  protected Command(String name, ProtocolEnum protocol) {
    this.name = name;
    if (Config.getInstance().isLanGame()) {
      this.protocol = protocol;
    } else {
      this.protocol = ProtocolEnum.TCP;
    }
  }

  /** Get the JSON representation of this command. */
  public String toJson() {
    return GSON.toJson(this);
  }

  public String getName() {
    return name;
  }

  public ProtocolEnum getProtocol() {
    return protocol;
  }

  /** Execute the command. */
  public abstract void execute();

  /** Set the object which is going to handle the execution of the command. */
  public abstract void setExecutor(Object executor);
}
