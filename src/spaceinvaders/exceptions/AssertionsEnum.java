package spaceinvaders.exceptions;

public enum AssertionsEnum {
  BOUNDED_TRANSFER_QUEUE("Transfer queue is bounded!"),
  NULL_ARGUMENT("Argument is null!"),
  UNBOUND_SOCKET("Socket is unbound!");

  private final String message;

  private AssertionsEnum(String message) {
    this.message = message;
  }

  @Override
  public String toString() {
    return message;
  }
}
