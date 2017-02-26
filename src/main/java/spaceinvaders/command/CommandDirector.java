package spaceinvaders.command;

import com.google.gson.JsonSyntaxException;
import spaceinvaders.exceptions.CommandNotFoundException;

/**
 * Used to convert JSON data into {@link spaceinvaders.command.Command}.
 *
 * @see spaceinvaders.command.CommandBuilder
 */
public class CommandDirector {
  private final CommandBuilder builder;

  /**
   * Constrct a director that uses the {@code builder} to make commands.
   *
   * @throws NullPointerException if builder is {@code null}.
   */
  public CommandDirector(CommandBuilder builder) {
    if (builder == null) {
      throw new NullPointerException();
    }
    this.builder = builder;
  }

  /**
   * Convert the {@code json} into a {@link spaceinvaders.command.Command}.
   *
   * @throws JsonSyntaxException if the specified JSON is invalid.
   * @throws CommandNotFoundException if the command could not be recognized.
   * @throws NullPointerException if argument is {@code null}.
   */
  public void makeCommand(String json) throws JsonSyntaxException, CommandNotFoundException {
    if (json == null) {
      throw new NullPointerException();
    }
    builder.buildCommand(json);
  }

  /**
   * Get the last succesfully built command.
   *
   * @return last command, or {@code null} if there was no such command.
   */
  public Command getCommand() {
    return builder.getCommand();
  }
}
