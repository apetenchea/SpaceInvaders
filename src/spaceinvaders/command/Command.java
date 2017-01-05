package spaceinvaders.command;

/**
 * Command to be executed by the client or by the server.
 */
public abstract class Command {
  private String name;

  /**
   * Construct a command with the specified name.
   */
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
  public abstract String toJson();
}
