package spaceinvaders.client;

/**
 * Errors that can appear in the client.
 */
public enum ErrorsEnum {
  AMBIGUOUS_CONFIG_SETTINGS(1,"All views must contain the same game configuration!"),
  BLOCKED_CONNECTION(2,"The connection is not allowed!"),
  BROKEN_CONNECTION(3,"The connection cannot be established!"), 
  INVALID_CONNCTION(3,"Server address or port is invalid!"),
  INVALID_SERVER_ADDRESS(4,"Server address is not a standard address!"),
  INVALID_SERVER_PORT(5,"Server port is not a standard port!"),
  INVALID_USER_NAME(6,
      "User name should start with a letter and contain between 2 and 10 letters or digits!"),
  LOST_CONNECTION(7,"Connection has been lost!"),
  SERVER_NOT_FOUND(8,"Server not found!"),
  SERVER_TIMEOUT(9,"The game server has timed out!"),
  UNEXPECTED_ERROR(9,"An unexpeced error has occured!");

  private final Integer code;
  private final String description;

  private ErrorsEnum(Integer code, String description) {
    this.code = code;
    this.description = description;
  }

  @Override
  public String toString() {
    return "Error " + code + ": " + description;
  }

  public Integer getCode() {
    return code;
  }

  public String getDescription() {
    return description;
  }
}
