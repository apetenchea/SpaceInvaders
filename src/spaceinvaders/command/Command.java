package spaceinvaders.command;

import com.google.gson.Gson;

/**
 * Command to be executed by the client or by the server.
 */
public abstract class Command {
  private String name;

  public Command(String name) {
    this.name = name;
  }

  /**
   * Execute the command.
   */
  public abstract void execute();

  /**
   * Get the JSON representation of this command.
   */
  public String toJson() {
    Gson gson = new Gson();
    return gson.toJson(this);
  }

  public String getName() {
    return name;
  }
}
