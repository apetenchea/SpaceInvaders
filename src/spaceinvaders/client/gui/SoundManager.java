package spaceinvaders.client.gui;

/** Plays sounds during the game. */
class SoundManager {
  /** Play a specific sound when the player shoots. */
  public void shooting() {
    //TODO
  }

  /** Play a specific sound when an invader is destroyed. */
  public void deadInvader() {
    //TODO
  }

  /** Play a specific sound when a player's ship is destroyed. */
  public void deadPlayer() {
    //TODO
  }

  /**
   * @param file - path to the sound resource.
   *
   * @throws NullPointerException - if argument is {@code null}.
   */
  private void playSound(String file) {
    if (file == null) {
      throw new NullPointerException();
    }
    //TODO
  }
}
