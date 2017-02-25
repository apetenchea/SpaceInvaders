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
import java.util.logging.Logger;
import spaceinvaders.client.ClientConfig;
import spaceinvaders.command.Command;
import spaceinvaders.command.server.MovePlayerLeftCommand;
import spaceinvaders.command.server.MovePlayerRightCommand;
import spaceinvaders.command.server.PlayerShootCommand;
import spaceinvaders.exceptions.IllegalPortNumberException;
import spaceinvaders.exceptions.InvalidServerAddressException;
import spaceinvaders.exceptions.InvalidUserNameException;
import spaceinvaders.utility.Chain;

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

  /**
   * Couple the controller with a {@link spaceinvaders.client.mvc.Model}.
   */
  public GameController(Model model) {
    model.addController(this);
    this.model = model;
  }

  @Override
  public void registerView(View view) {
    if (!views.contains(view)) {
      view.addStartGameListener(new StartGameListener());
      view.addQuitAppListener(new CloseAppListener());
      view.addKeyListener(
          new KeyPressListener(
          new MoveLeftListener(
          new MoveRightListener(
          new ShootListener(
          new QuitGameListener(null))))));
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
    } else {
      // This should never happen.
      throw new AssertionError();
    }
  }

  private void displayErrorOnViews(Exception exception) {
    for (View view : views) {
      view.showMenu();
      view.displayError(exception);
    }
  }

  /** User starts a new game. */
  private class StartGameListener implements ActionListener {
    public void actionPerformed(ActionEvent event) {
      if (views.size() == 0) {
        // This should never happen.
        throw new AssertionError();
      }

      try {
        views.get(0).setConfig();
      } catch (IllegalPortNumberException portException) {
        displayErrorOnViews(portException);
        return;
      }
      ClientConfig config = ClientConfig.getInstance();
      try {
        config.verify();
      } catch (InvalidServerAddressException | IllegalPortNumberException
          | InvalidUserNameException exception) {
        displayErrorOnViews(exception);
        LOGGER.log(SEVERE,exception.toString(),exception);
        return;
      }
      Future<?> modelFuture = modelExecutor.submit(model);
      modelStateChecker.submit(new Callable<Void>() {
        @Override
        public Void call() {
          final int checkingRateMs = 500;
          while (!modelFuture.isDone()) {
            try {
              Thread.sleep(checkingRateMs);
            } catch (InterruptedException intException) {
              if (!shuttingDown) {
                LOGGER.log(SEVERE,intException.toString(),intException);
              }
              model.exitGame();
              break;
            }
          }
          try {
            modelFuture.get();
          } catch (Exception ex) {
            if (!shuttingDown) {
              displayErrorOnViews((Exception) ex.getCause());
              LOGGER.log(SEVERE,ex.toString(),ex);
            }
          } finally {
            model.exitGame();
          }
          return null;
        }
      });
    }
  }

  /** User close the application. */
  private class CloseAppListener implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent event) {
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

  /** Handle key press events. */
  private class KeyPressListener extends KeyAdapter implements Chain<KeyEvent> {
    private Chain<KeyEvent> nextChain;

    public KeyPressListener(Chain<KeyEvent> nextChain) {
      this.nextChain = nextChain;
    }
    
    @Override
    public void keyPressed(KeyEvent event) {
      handle(event);
    }

    @Override
    public void handle(KeyEvent event) {
      // Slide.
      nextChain.handle(event);
    }

    @Override
    public void setNext(Chain<KeyEvent> nextChain) {
      this.nextChain = nextChain;
    }
  }

  /** Player moves left. */
  private class MoveLeftListener implements Chain<KeyEvent> {
    private Chain<KeyEvent> nextChain;

    public MoveLeftListener(Chain<KeyEvent> nextChain) {
      this.nextChain = nextChain;
    }

    @Override
    public void handle(KeyEvent event) {
      if (event.getKeyCode() == VK_LEFT) {
        if (model.getGameState()) {
          model.doCommand(new MovePlayerLeftCommand(ClientConfig.getInstance().getId()));
        }
      } else {
        if (nextChain != null) {
          nextChain.handle(event);
        }
      }
    }

    @Override
    public void setNext(Chain<KeyEvent> nextChain) {
      this.nextChain = nextChain;
    }
  }

  /** Player moves right. */
  private class MoveRightListener implements Chain<KeyEvent> {
    private Chain<KeyEvent> nextChain;

    public MoveRightListener(Chain<KeyEvent> nextChain) {
      this.nextChain = nextChain;
    }

    @Override
    public void handle(KeyEvent event) {
      if (event.getKeyCode() == VK_RIGHT) {
        if (model.getGameState()) {
          model.doCommand(new MovePlayerRightCommand(ClientConfig.getInstance().getId()));
        }
      } else {
        if (nextChain != null) {
          nextChain.handle(event);
        }
      }
    }

    @Override
    public void setNext(Chain<KeyEvent> nextChain) {
      this.nextChain = nextChain;
    }
  }

  /** Player shoots a bullet. */
  private class ShootListener implements Chain<KeyEvent> {
    private Chain<KeyEvent> nextChain;

    public ShootListener(Chain<KeyEvent> nextChain) {
      this.nextChain = nextChain;
    }

    @Override
    public void handle(KeyEvent event) {
      if (event.getKeyCode() == VK_SPACE) {
        if (model.getGameState()) {
          model.doCommand(new PlayerShootCommand(ClientConfig.getInstance().getId()));
        }
      } else {
        if (nextChain != null) {
          nextChain.handle(event);
        }
      }
    }

    @Override
    public void setNext(Chain<KeyEvent> nextChain) {
      this.nextChain = nextChain;
    }
  }

  /** Player quits the game. */
  private class QuitGameListener implements Chain<KeyEvent> {
    private Chain<KeyEvent> nextChain;

    public QuitGameListener(Chain<KeyEvent> nextChain) {
      this.nextChain = nextChain;
    }

    @Override
    public void handle(KeyEvent event) {
      if (event.getKeyCode() == VK_ESCAPE) {
        model.exitGame();
        for (View view : views) {
          view.showMenu();
        }
      } else {
        if (nextChain != null) {
          nextChain.handle(event);
        }
      }
    }

    @Override
    public void setNext(Chain<KeyEvent> nextChain) {
      this.nextChain = nextChain;
    }
  }
}
