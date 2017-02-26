package spaceinvaders.client.mvc;

import java.util.List;
import java.util.Observer;

/**
 * Separates application data and user interface.
 *
 * @see spaceinvaders.client.mvc.Model
 * @see spaceinvaders.client.mvc.View
 */
public interface Controller extends Observer {
  /**
   * Register a view with this controller. 
   *
   * @param view the view to be registered.
   */
  public void registerView(View view);

  /**
   * Get the {@link spaceinvaders.client.mvc.Model} registered with this controller.
   */
  public Model getModel(); 

  /**
   * Get a list of all {@link spaceinvaders.client.mvc.View views} registered with this
   * controller.
   */
  public List<View> getViews();
}
