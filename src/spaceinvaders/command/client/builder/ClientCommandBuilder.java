package spaceinvaders.command.client.builder;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.Gson;
import java.util.Collections;
import java.util.Map;
import java.util.HashMap;
import spaceinvaders.command.Command;
import spaceinvaders.command.CommandBuilder;
import spaceinvaders.command.client.InitGameWorldCommand;
import spaceinvaders.command.client.InitPlayersCommand;
import spaceinvaders.command.client.SetPlayerIdCommand;

/**
 * Builds commands for the client.
 */
public class ClientCommandBuilder implements CommandBuilder {
  private static final Map<String,Command> COMMAND_MAP;
  static {
    Map<String,Command> commandMap = new HashMap<String,Command>();
    commandMap.put(new SetPlayerIdCommand().getName(),new SetPlayerIdCommand());
    commandMap.put(new InitGameWorldCommand().getName(),new InitGameWorldCommand());
    commandMap.put(new InitPlayersCommand().getName(),new InitPlayersCommand());
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
