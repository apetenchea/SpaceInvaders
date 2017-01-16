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
  private final Gson gson = new Gson();
  private final JsonParser parser = new JsonParser();
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
   * Build a command out of JSON.
   *
   * @throws JsonSyntaxException - if the specified JSON is not valid.
   * @throws CommandNotFoundException - if the command could not be recognized.
   * @throws NullPointerException - if the specified JSON is <code>null</code>.
   */
  public void buildCommand(String json) throws JsonSyntaxException, CommandNotFoundException {
    if (json == null) {
      throw new NullPointerException();
    }
    JsonObject jsonObj = parser.parse(json).getAsJsonObject();
    String key = jsonObj.get("name").getAsString();
    Command value = commandMap.get(key);
    if (value == null) {
      throw new CommandNotFoundException();
    }
    command = gson.fromJson(json,value.getClass());
  }

  /**
   * Get the built command.
   */
  public Command getCommand() {
    return command;
  }
}
