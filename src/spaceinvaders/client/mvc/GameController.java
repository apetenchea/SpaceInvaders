package spaceinvaders.client.mvc;

import static java.awt.event.KeyEvent.VK_ESCAPE;

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

  private ExecutorService updateViewsExecutor;

  /**
   * Construct a controller and couple it with a model.
   */
  public GameController(Model model) {
    model.addController(this);
    this.model = model;
    views = new ArrayList<>();

    updateViewsExecutor = Executors.newCachedThreadPool();
  }

  @Override
  public void registerView(View view) {
    if (!views.contains(view)) {
      view.addStartGameListener(new StartGameListener());
      view.addQuitAppListener(new QuitAppListener());
      view.addQuitGameListener(new QuitGameListener());
      views.add(view);
    }
  }

  @Override
  public void update(Observable obs, Object arg) {
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

  @Override
  public void configurePlayer(int id) {
    ClientConfig config = ClientConfig.getInstance();
    model.setPlayerId(id);
    model.startSendingPackets();
    model.doCommand(new ConfigurePlayerCommand(config.getUserName(),config.getTeamSize()));
  }

  private void displayErrorOnViews(Exception exception) {
    for (View view : views) {
      view.displayError(exception);
    }
  }

  private void updateViews(String data) {
    LOGGER.info("Update views" + data);
  }

  private class QuitAppListener implements ActionListener {
    public void actionPerformed(ActionEvent event) {
      System.err.println("Quit");
      model.exitGame();
      model.shutdown();
      for (View view : views) {
        view.shutdown();
      }
      updateViewsExecutor.shutdownNow();
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
