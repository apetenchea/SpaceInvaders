package spaceinvaders.command;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import spaceinvaders.command.Command;
import spaceinvaders.exceptions.CommandNotFoundException;

/** Builds commands. */
public abstract class CommandBuilder {
  private static final Gson GSON = new Gson();
  private static final JsonParser PARSER = new JsonParser();
  private Map<String,Command> commandMap;
  private Command command;

  /** Create a builder capable of building the specified commands. */
  public CommandBuilder(Command ... commands) {
    commandMap = new HashMap<>();
    for (Command command : commands) {
      commandMap.put(command.getName(),command);
    }
    commandMap = Collections.unmodifiableMap(commandMap);
  }

  /**
   * Build a command from a {@code json}.
   *
   * @throws JsonSyntaxException if the specified JSON is not valid.
   * @throws CommandNotFoundException if the command could not be recognized.
   * @throws NullPointerException if argument is {@code null}.
   */
  public void buildCommand(String json) throws JsonSyntaxException, CommandNotFoundException {
    if (json == null) {
      throw new NullPointerException();
    }
    JsonObject jsonObj = PARSER.parse(json).getAsJsonObject();
    String key = jsonObj.get("name").getAsString();
    Command value = commandMap.get(key);
    if (value == null) {
      throw new CommandNotFoundException();
    }
    command = GSON.fromJson(json,value.getClass());
  }

  /** Get the last command built. */
  public Command getCommand() {
    return command;
  }
}
