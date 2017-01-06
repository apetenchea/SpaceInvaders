package spaceinvaders.command;

/**
 * Used for building commands.
 */
public interface CommandBuilder {
  /**
   * Build a command out of a JSON.
   */
  public void buildCommand(String json);

  /**
   * Get the built command.
   */
  public Command getCommand();
}
