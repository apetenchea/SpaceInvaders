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
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import spaceinvaders.client.ClientConfig;
import spaceinvaders.command.Command;
import spaceinvaders.command.CommandDirector;
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

  private final Model model;
  private final List<View> views = new ArrayList<>();
  private final ExecutorService modelExecutor = Executors.newSingleThreadExecutor();
  private final ExecutorService modelStateChecker = Executors.newSingleThreadExecutor();
  private Boolean shuttingDown = false;

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

      views.get(0).setConfig();
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
        Future<?> modelFuture = modelExecutor.submit(model);
        modelStateChecker.submit(new Callable<Void>() {
          @Override
          public Void call() {
            final int checkingRateMs = 1000;
            while (!modelFuture.isDone()) {
              try {
                Thread.sleep(checkingRateMs);
                modelFuture.get();
              } catch (Exception ex) {
                if (!shuttingDown) {
                  LOGGER.log(SEVERE,ex.toString(),ex);
                  model.exitGame();
                  break;
                }
              }
            }
            return null;
          }
        });
      } catch (Exception exception) {
        LOGGER.log(SEVERE,exception.toString(),exception);
      }
    }
  }

  private class QuitAppListener implements ActionListener {
    public void actionPerformed(ActionEvent event) {
      System.err.println("Quit");
      shuttingDown = true;
      model.exitGame();
      model.shutdown();
      for (View view : views) {
        view.shutdown();
      }
      modelExecutor.shutdownNow();
      modelStateChecker.shutdownNow();
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
        if (model.getGameState()) {
          model.doCommand(new MovePlayerLeftCommand(ClientConfig.getInstance().getId()));
        }
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
        if (model.getGameState()) {
          model.doCommand(new MovePlayerRightCommand(ClientConfig.getInstance().getId()));
        }
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
        if (model.getGameState()) {
          model.doCommand(new PlayerShootCommand(ClientConfig.getInstance().getId()));
        }
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
