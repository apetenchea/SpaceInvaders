package spaceinvaders.command;

import com.google.gson.Gson;

/**  Command to be executed by the client or by the server. */
public abstract class Command {
  private static final transient Gson GSON = new Gson();

  private final transient ProtocolEnum protocol;
  private final String name;

  public Command(String name, ProtocolEnum protocol) {
    this.name = name;
    this.protocol = protocol;
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

  /** Set the object upon which execution takes place. */
  public abstract void setExecutor(Object executor);
}
