package spaceinvaders.command;

/**
 * Creates commands using the CommandBuilder.
 *
 * @see spaceinvaders.command.CommandBuilder
 */
public class CommandDirector {
  private CommandBuilder builder;

  public CommandDirector(CommandBuilder builder) {
    this.builder = builder;
  }

  public void makeCommand(String json) {
    builder.buildCommand(json);
  }

  public Command getCommand() {
    return builder.getCommand();
  }
}
