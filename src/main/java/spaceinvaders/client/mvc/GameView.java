package spaceinvaders.client.mvc;

import java.awt.event.ActionListener;
import java.awt.event.KeyListener;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import spaceinvaders.client.gui.GameGraphics;
import spaceinvaders.client.gui.Menu;
import spaceinvaders.game.Entity;
import spaceinvaders.game.EntityEnum;
import spaceinvaders.utility.Couple;

/**
 * User interface.
 *
 * @see spaceinvaders.client.mvc.GameController
 * @see spaceinvaders.client.mvc.GameModel
 */
public class GameView implements View {
  private final GameGraphics game = new GameGraphics();
  private final Menu menu = new Menu();
  private JFrame currentFrame;

  @Override
  public void addStartGameListener(ActionListener listener) {
    menu.addPlayListener(listener);
  }

  @Override
  public void addQuitAppListener(ActionListener listener) {
    menu.addQuitListener(listener);
  }

  @Override
  public void addKeyListener(KeyListener listener) {
    game.addKeyListener(listener);
  }

  @Override
  public void setConfig() {
    menu.setConfig();
  }

  @Override
  public void displayError(Exception exception) {
    JOptionPane.showMessageDialog(
        currentFrame,
        exception.toString(),
        exception.getMessage(),
        JOptionPane.ERROR_MESSAGE);
  }
  
  @Override
  public void flush() {
    game.flush();
  }

  @Override
  public void showGame() {
    menu.hide();
    game.init();
    game.show();
    currentFrame = game.getFrame();
  }

  @Override
  public void showMenu() {
    game.hide();
    menu.show();
    currentFrame = menu.getFrame();
  }

  @Override
  public void startGame() {
    game.setMessage("Game On!");
  }

  @Override
  public void gameOver() {
    game.setMessage("Your ship was destroyed!");
  }

  @Override
  public void youWon() {
    game.setMessage("<html>Congratulations!<br>Your team is victorious!</html>");
    game.showVictoryImage();
  }

  @Override
  public void youLost() {
    game.setMessage("Your team was destroyed!");
    game.showDefeatImage();
  }

  @Override
  public void changeScore(int playerId, int value) {
    game.changeScore(playerId,value);
  }

  @Override
  public void setPlayerNames(List<Couple<Integer,String>> players) {
    game.setPlayerNames(players);
  }

  @Override
  public void setFrameContent(List<Entity> content) {
    game.setFrameContent(content);
  }

  @Override
  public void moveEntity(int entityId, int newX, int newY) {
    game.moveEntity(entityId,newX,newY);
  }

  @Override
  public void spawnEntity(int id, EntityEnum type, int posX, int posY) {
    game.spawnEntity(id,type,posX,posY);
  }

  @Override
  public void wipeOutEntity(int id) {
    game.wipeOutEntity(id);
  }

  @Override
  public void translateGroup(EntityEnum type, int offsetX, int offsetY) {
    game.translateGroup(type,offsetX,offsetY);
  }

  @Override
  public void shutdown() {
    menu.destroy();
    game.destroy();
  }
}
