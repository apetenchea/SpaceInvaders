package spaceinvaders.client.gui.entities;

import static spaceinvaders.game.EntityEnum.PLAYER;

import spaceinvaders.client.ResourcesConfig;

/** The player character. */
public class Player extends GraphicalEntity {
  private String name;

  public Player() {
    super(ResourcesConfig.getInstance().getAvatars(PLAYER));
  }

  @Override
  public void draw(GraphicalEntityVisitor visitor) {
    visitor.visit(this);
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}
