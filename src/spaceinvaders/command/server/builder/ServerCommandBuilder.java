package spaceinvaders.command.server.builder;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.Gson;
import java.util.Collections;
import java.util.Map;
import java.util.HashMap;
import spaceinvaders.command.Command;
import spaceinvaders.command.CommandBuilder;
import spaceinvaders.command.server.ConfigurePlayerCommand;

/**
 * Builds commands for the server.
 */
public class ServerCommandBuilder implements CommandBuilder {
  private static final Map<String,Command> COMMAND_MAP;
  static {
    Map<String,Command> commandMap = new HashMap<String,Command>();
    commandMap.put(new ConfigurePlayerCommand().getName(),new ConfigurePlayerCommand());
    COMMAND_MAP = Collections.unmodifiableMap(commandMap);
  }

  private Command command;

  @Override
  public void buildCommand(String json) {
    JsonParser parser = new JsonParser();
    JsonObject jsonObj = parser.parse(json).getAsJsonObject();
    String key = jsonObj.get("name").getAsString();
    Command value = COMMAND_MAP.get(key);
    Gson gson = new Gson();
    command = gson.fromJson(json,value.getClass());
  }

  @Override
  public Command getCommand() {
    return command;
  }
}
