package spaceinvaders.client.mvc;

import static java.awt.event.KeyEvent.VK_ESCAPE;
import static java.awt.event.KeyEvent.VK_LEFT;
import static java.awt.event.KeyEvent.VK_RIGHT;
import static java.awt.event.KeyEvent.VK_SPACE;
import static java.util.logging.Level.SEVERE;

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
  private final List<View> views = new ArrayList<>();

  /** Construct a controller and couple it with a model. */
  public GameController(Model model) {
    model.addController(this);
    this.model = model;
  }

  @Override
  public void registerView(View view) {
    if (!views.contains(view)) {
      view.addStartGameListener(new StartGameListener());
      view.addQuitAppListener(new QuitAppListener());
      view.addKeyListener(
          new MoveLeftListener(
            new MoveRightListener(
              new ShootListener(
                new QuitGameListener(null)))));
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
  public void update(Observable obs, Object arg) {
    if (arg instanceof Command) {
      Command command = (Command) arg;
      command.setExecutor(this);
      command.execute();
    }
    if (arg == null) {
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

  private class StartGameListener implements ActionListener {
    public void actionPerformed(ActionEvent event) {
      assert views.size() > 0;
      LOGGER.info("Play");

      ClientConfig config = ClientConfig.getInstance();;
      try {
        config.verify();
      } catch (InvalidServerAddressException | IllegalPortNumberException
          | InvalidUserNameException exception) {
        displayErrorOnViews(exception);
        LOGGER.log(SEVERE,exception.toString(),exception);
        return;
      }
      try {
        model.call();
      } catch (Exception exception) {
        LOGGER.log(SEVERE,exception.toString(),exception);
        model.exitGame();
      }
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

  private abstract class ChainListener extends KeyAdapter {
    private ChainListener nextChain;

    public ChainListener(ChainListener nextChain) {
      this.nextChain = nextChain;
    }

    public void pass(KeyEvent event) {
      if (nextChain == null) {
        throw new AssertionError();
      }
      nextChain.keyPressed(event);
    }
  }

  private class MoveLeftListener extends ChainListener {
    public MoveLeftListener(ChainListener nextChain) {
      super(nextChain);
    }

    @Override
    public void keyPressed(KeyEvent event) {
      if (event.getKeyCode() == VK_LEFT) {
        model.doCommand(new MovePlayerLeftCommand(ClientConfig.getInstance().getId()));
      } else {
        pass(event);
      }
    }
  }

  private class MoveRightListener extends ChainListener {
    public MoveRightListener(ChainListener nextChain) {
      super(nextChain);
    }

    @Override
    public void keyPressed(KeyEvent event) {
      if (event.getKeyCode() == VK_RIGHT) {
        model.doCommand(new MovePlayerRightCommand(ClientConfig.getInstance().getId()));
      } else {
        pass(event);
      }
    }
  }

  private class ShootListener extends ChainListener {
    public ShootListener(ChainListener nextChain) {
      super(nextChain);
    }

    @Override
    public void keyPressed(KeyEvent event) {
      if (event.getKeyCode() == VK_SPACE) {
        model.doCommand(new PlayerShootCommand(ClientConfig.getInstance().getId()));
      } else {
        pass(event);
      }
    }
  }

  private class QuitGameListener extends ChainListener {
    public QuitGameListener(ChainListener nextChain) {
      super(nextChain);
    }

    @Override
    public void keyPressed(KeyEvent event) {
      if (event.getKeyCode() == VK_ESCAPE) {
        model.exitGame();
        for (View view : views) {
          view.showMenu();
        }
      } else {
        pass(event);
      }
    }
  }
}
