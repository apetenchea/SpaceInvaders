package spaceinvaders.command;

import com.google.gson.Gson;

/**  Command to be executed by the client or by the server. */
public abstract class Command {
  private static final transient Gson GSON = new Gson();

  private final transient ProtocolEnum protocol;
  private final String name;

  public Command(String name) {
    this.name = name;
    this.protocol = ProtocolEnum.TCP;
  }

  public Command(String name, ProtocolEnum protocol) {
    this.name = name;
    this.protocol = protocol;
  }

  /** Execute the command. */
  public abstract void execute();

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

  public abstract void setExecutor(Object executor);
}
