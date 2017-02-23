package spaceinvaders.client.gui;

import static spaceinvaders.game.EntityEnum.INVADER;
import static spaceinvaders.game.EntityEnum.PLAYER;
import static spaceinvaders.game.EntityEnum.PLAYER_BULLET;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;
import javax.swing.JPanel;
import spaceinvaders.client.gui.entities.Drawable;
import spaceinvaders.client.gui.entities.GraphicalEntity;
import spaceinvaders.client.gui.entities.GraphicalEntityVisitor;
import spaceinvaders.client.gui.entities.GraphicsFactory;
import spaceinvaders.client.gui.entities.PaintingVisitor;
import spaceinvaders.client.gui.entities.Player;
import spaceinvaders.game.Entity;
import spaceinvaders.game.EntityEnum;

/**
 * Main panel of the game.
 *
 * <p>Contains all the visible elements of the game. Controls painting and repainting.
 */
@SuppressWarnings("serial")
class GamePanel extends JPanel {
  private final GraphicsFactory factory = GraphicsFactory.getInstance();
  private final ConcurrentNavigableMap<Integer,GraphicalEntity> entityMap =
    new ConcurrentSkipListMap<>();
  private final SoundManager sound = new SoundManager();
  private Integer playerAvatarNumber;
  
  public GamePanel() {
    setBackground(Color.BLACK);
    setForeground(Color.BLACK);
  }

  @Override
  protected void paintComponent(Graphics graphics) {
    super.paintComponent(graphics);
    final GraphicalEntityVisitor painter = new PaintingVisitor(graphics,this);
    graphics.setColor(Color.WHITE);
    graphics.setFont(new Font("Courier",Font.BOLD,15));
    for (Drawable entity : entityMap.values()) {
      entity.draw(painter);
    }
  }

  public void init() {
    playerAvatarNumber = 0;
    entityMap.clear();
  }

  /**
   * Refresh all entities by checking them against a list of updates.
   *
   * @throws NullPointerException - if argument is {@code null}.
   */
  public void refreshEntities(List<Entity> updates) {
    if (updates == null) {
      throw new NullPointerException();
    }
    final List<Integer> elim = new ArrayList<>();
    final boolean[] mark = new boolean[updates.size()];
    for (Map.Entry<Integer,GraphicalEntity> entry : entityMap.entrySet()) {
      boolean found = false;
      int index = 0;
      for (Entity it : updates) {
        if (entry.getKey().equals(it.getId())) {
          found = true;
          entry.getValue().relocate(it.getX(),it.getY());
          mark[index] = true;
          break;
        }
        ++index;
      }
      if (!found) {
        elim.add(entry.getKey());
      }
    }
    
    /* Remove entities not found in the updates. */
    for (int key : elim) {
      entityMap.remove(key);
    }

    /* Add entities not found in the map. */
    int index = 0;
    for (Entity it : updates) {
      if (!mark[index]) {
        GraphicalEntity spawned = factory.create(it);
        entityMap.put(it.getId(),spawned);
      }
      ++index;
    }
  }

  /**
   * @param id - player ID.
   * @param name - player name.
   *
   * @throws NullPointerException - if an argument is {@code null} of if the {@code id} could not
   *     be found.
   */
  public void setPlayer(int id, String name) {
    if (name == null) {
      throw new NullPointerException();
    }
    final Player player = (Player) entityMap.get(id);
    if (player == null) {
      throw new NullPointerException();
    }
    player.setName(name);
    player.setAvatarNumber(playerAvatarNumber++);
  }

  /**
   * Change the spatial coordinates of an entity.
   *
   * @param id - entity ID.
   * @param newX - new coordinate on X Axis.
   * @param newY - new coordinate on Y Axis.
   *
   * @throws NullPointerException - if {@code id} could not be found.
   */
  public void relocateEntity(int id, int newX, int newY) {
    final GraphicalEntity entity = entityMap.get(id);
    if (entity == null) {
      throw new NullPointerException();
    }
    entity.relocate(newX,newY);
  }

  /**
   * Create a new entity.
   *
   * <p>If an entity already exists with the {@code id}, it is overwritten.
   *
   * @param id - entity ID.
   * @param type - type of the entity.
   * @param posX - coordinate on X Axis.
   * @param posY - coordinate on Y Axis.
   */
  public void spawnEntity(int id, EntityEnum type, int posX, int posY) {
    entityMap.put(id,factory.create(new Entity(type,id,posX,posY)));
    if (type.equals(PLAYER_BULLET)) {
      sound.shooting();
    }
  }

  /**
   * Remove an entity.
   *
   * @param id - ID of the entity to be removed.
   *
   * @throws NullPointerException - if the {@code id} could not be found.
   */
  public void wipeOutEntity(int id) {
    GraphicalEntity entity = entityMap.remove(id);
    if (entity == null) {
      throw new NullPointerException();
    }
    if (entity.getType().equals(INVADER)) {
      sound.deadInvader();
    } else if (entity.getType().equals(PLAYER)) {
      sound.deadPlayer();
    }
  }

  /**
   * Translate an entire group of entities, all of the same {@code type}.
   *
   * @param type - type of all entities in the group.
   * @param offsetX - offset on X Axis.
   * @param offsetY - offset on Y Axis.
   */
  public void translateGroup(EntityEnum type, int offsetX, int offsetY) {
    for (GraphicalEntity entity : entityMap.values()) {
      if (entity.getType().equals(type)) {
        entity.translate(offsetX,offsetY);
      }
    }
  }
}
