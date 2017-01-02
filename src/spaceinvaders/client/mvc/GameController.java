package spaceinvaders.client.mvc;

import static java.awt.event.KeyEvent.VK_ESCAPE;
import static spaceinvaders.client.ErrorsEnum.AMBIGUOUS_CONFIG_SETTINGS;
import static spaceinvaders.client.ErrorsEnum.BLOCKED_CONNECTION;
import static spaceinvaders.client.ErrorsEnum.BROKEN_CONNECTION;
import static spaceinvaders.client.ErrorsEnum.INVALID_CONNCTION;
import static spaceinvaders.client.ErrorsEnum.INVALID_SERVER_ADDRESS;
import static spaceinvaders.client.ErrorsEnum.INVALID_SERVER_PORT;
import static spaceinvaders.client.ErrorsEnum.INVALID_USER_NAME;
import static spaceinvaders.client.ErrorsEnum.SERVER_NOT_FOUND;
import static spaceinvaders.client.ErrorsEnum.UNEXPECTED_ERROR;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import spaceinvaders.client.ClientConfig;
import spaceinvaders.client.ErrorsEnum;
import spaceinvaders.client.network.ConnectionNotAllowedException;
import spaceinvaders.client.network.InvalidConnectionConfigurationException;
import spaceinvaders.client.network.ServerNotFoundException;
import spaceinvaders.client.network.SocketIoException;

/**
 * Handles communication between one or more views and a model.
 *
 * @see spaceinvaders.client.mvc.GameModel
 * @see spaceinvaders.client.mvc.GameView
 */
public class GameController implements Controller {
  private Model model;
  private List<View> views;
  private Lock updateViewsLock;
  private ExecutorService executor;

  /**
   * Construct a controller and couple it with a model.
   */
  public GameController(Model model) {
    this();
    model.addController(this);
    this.model = model;
  }

  private GameController() {
    views = new ArrayList<>();
    updateViewsLock = new ReentrantLock();
    executor = Executors.newCachedThreadPool();
  }

  /**
   * Prepare and register a view. 
   */
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
    System.err.println("controller nofified");
    if (arg == null) {
      executor.execute(new Runnable() {
        @Override
        public void run() {
          updateViews();
        }
      });
    } else {
      displayErrorOnViews((ErrorsEnum)arg);
      try {
        model.exitGame();
      } catch (SocketIoException exception) {
        displayErrorOnViews(UNEXPECTED_ERROR);
      }
      for (View view : views) {
        view.showMenu();
      }
    }
  }

  private void displayErrorOnViews(ErrorsEnum error) {
    for (View view : views) {
      view.displayError(error);
    }
  }

  private void updateViews() {
    updateViewsLock.lock();
    String[] updates = model.getData();
    for (String data : updates) {
      for (View view : views) {
        executor.execute(new Runnable() {
          @Override
          public void run() {
            view.update(data);
          }
        });
      }
    }
    updateViewsLock.unlock();
  }

  private class QuitAppListener implements ActionListener {
    public void actionPerformed(ActionEvent event) {
      System.out.println("Quit");
      try {
        model.exitGame();
      } catch (SocketIoException exception) {
        // The app is going to exit anyway, so do nothing in this case.
      }
      model.shutdown();
      for (View view : views) {
        view.shutdown();
      }
      executor.shutdown();
    }
  }

  private class QuitGameListener extends KeyAdapter {
    @Override
    public void keyPressed(KeyEvent event) {
      if (event.getKeyCode() == VK_ESCAPE) {
        try {
          model.exitGame();
        } catch (SocketIoException exception) {
          displayErrorOnViews(UNEXPECTED_ERROR);
        }
        for (View view : views) {
          view.showMenu();
        }
      }
    }
  }

  private class StartGameListener implements ActionListener {
    public void actionPerformed(ActionEvent event) {
      assert views.size() > 0;
      System.out.println("Play");

      ClientConfig config;
      List<ClientConfig> viewConfigs = new ArrayList<>();
      for (View view : views) {
        config = view.getConfig();
        if (!viewConfigs.contains(config)) {
          viewConfigs.add(config);
        }
        if (viewConfigs.size() > 1) {
          break;
        }
      }
      if (viewConfigs.size() == 1) {
        config = viewConfigs.get(0);
        if (!config.isAddrValid()) {
          displayErrorOnViews(INVALID_SERVER_ADDRESS);
          return;
        }
        if (!config.isPortValid()) {
          displayErrorOnViews(INVALID_SERVER_PORT);
          return;
        }
        if (!config.isUserNameValid()) {
          displayErrorOnViews(INVALID_USER_NAME);
          return;
        }
        boolean gameExceptionFlag = false;
        try {
          model.initNewGame(config);
        } catch (ConnectionNotAllowedException exception) {
          displayErrorOnViews(BLOCKED_CONNECTION);
          gameExceptionFlag = true;
        } catch (InvalidConnectionConfigurationException exception) {
          displayErrorOnViews(INVALID_CONNCTION); 
          gameExceptionFlag = true;
        } catch (ServerNotFoundException exception) {
          displayErrorOnViews(SERVER_NOT_FOUND);
          gameExceptionFlag = true;
        } catch (SocketIoException exception) {
          displayErrorOnViews(BROKEN_CONNECTION);
          gameExceptionFlag = true;
        }
        if (gameExceptionFlag) {
          try {
            model.exitGame();
          } catch (SocketIoException exception) {
            displayErrorOnViews(UNEXPECTED_ERROR);
          }
        } else {
          for (View view : views) {
            view.showGame();
          }
        }
      } else {
        displayErrorOnViews(AMBIGUOUS_CONFIG_SETTINGS);
      }
    }
  }
}
