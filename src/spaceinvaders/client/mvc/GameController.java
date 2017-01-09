package spaceinvaders.client.mvc;

import static java.awt.event.KeyEvent.VK_ESCAPE;
import static java.awt.event.KeyEvent.VK_LEFT;
import static java.awt.event.KeyEvent.VK_RIGHT;
import static java.awt.event.KeyEvent.VK_SPACE;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import spaceinvaders.client.ClientConfig;
import spaceinvaders.command.Command;
import spaceinvaders.command.CommandDirector;
import spaceinvaders.command.client.builder.ClientCommandBuilder;
import spaceinvaders.command.server.ConfigurePlayerCommand;
import spaceinvaders.exceptions.IllegalPortNumberException;
import spaceinvaders.exceptions.InvalidServerAddressException;
import spaceinvaders.exceptions.InvalidUserNameException;
import spaceinvaders.game.Entity;
import spaceinvaders.utility.Couple;
import spaceinvaders.command.server.MovePlayerLeftCommand;
import spaceinvaders.command.server.MovePlayerRightCommand;
import spaceinvaders.command.server.PlayerShootCommand;

/**
 * Handles communication between one or more views and a model.
 *
 * @see spaceinvaders.client.mvc.GameModel
 * @see spaceinvaders.client.mvc.GameView
 */
public class GameController implements Controller {
  private static final Logger LOGGER = Logger.getLogger(GameController.class.getName());

  private Model model;
  private List<View> views;

  /**
   * Construct a controller and couple it with a model.
   */
  public GameController(Model model) {
    model.addController(this);
    this.model = model;
    views = new ArrayList<>();
  }

  @Override
  public void registerView(View view) {
    if (!views.contains(view)) {
      view.addStartGameListener(new StartGameListener());
      view.addQuitAppListener(new QuitAppListener());
      view.addQuitGameListener(new QuitGameListener());
      view.addMoveLeftListener(new MoveLeftListener());
      view.addMoveRightListener(new MoveRightListener());
      view.addShootListener(new ShootListener());
      views.add(view);
    }
  }

  @Override
  public Model getModel() {
    return model;
  }

  @Override
  public List<View> getViews() {
    return views;
  }

  @Override
  public synchronized void update(Observable obs, Object arg) {
    if (arg instanceof String) {
      CommandDirector director = new CommandDirector(new ClientCommandBuilder());
      director.makeCommand((String) arg);
      Command command = director.getCommand();
      command.setExecutor(this);
      command.execute();
    } else if (arg instanceof Exception) {
      displayErrorOnViews((Exception) arg);
      model.exitGame();
      for (View view : views) {
        view.showMenu();
      }
    }
  }

  private void displayErrorOnViews(Exception exception) {
    for (View view : views) {
      view.showMenu();
      view.displayError(exception);
    }
  }

  private class QuitAppListener implements ActionListener {
    public void actionPerformed(ActionEvent event) {
      System.err.println("Quit");
      model.exitGame();
      model.shutdown();
      for (View view : views) {
        view.shutdown();
      }
    }
  }

  private class QuitGameListener extends KeyAdapter {
    @Override
    public void keyPressed(KeyEvent event) {
      if (event.getKeyCode() == VK_ESCAPE) {
        model.exitGame();
        for (View view : views) {
          view.showMenu();
        }
      }
    }
  }

  private class MoveLeftListener extends KeyAdapter {
    @Override
    public void keyPressed(KeyEvent event) {
      if (event.getKeyCode() == VK_LEFT) {
        model.doCommand(new MovePlayerLeftCommand(ClientConfig.getInstance().getId()));
      }
    }
  }

  private class MoveRightListener extends KeyAdapter {
    @Override
    public void keyPressed(KeyEvent event) {
      if (event.getKeyCode() == VK_RIGHT) {
        model.doCommand(new MovePlayerRightCommand(ClientConfig.getInstance().getId()));
      }
    }
  }

  private class ShootListener extends KeyAdapter {
    @Override
    public void keyPressed(KeyEvent event) {
      if (event.getKeyCode() == VK_SPACE) {
        model.doCommand(new PlayerShootCommand(ClientConfig.getInstance().getId()));
      }
    }
  }

  private class StartGameListener implements ActionListener {
    public void actionPerformed(ActionEvent event) {
      assert views.size() > 0;
      LOGGER.info("Play");

      ClientConfig config;
      config = views.get(0).getConfig();
      try {
        config.verify();
      } catch (InvalidServerAddressException | IllegalPortNumberException
          | InvalidUserNameException exception) {
        displayErrorOnViews(exception);
        LOGGER.log(Level.SEVERE,exception.getMessage(),exception);
        return;
      }
      model.initNewGame();
    }
  }
}
