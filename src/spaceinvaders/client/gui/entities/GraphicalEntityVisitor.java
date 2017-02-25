package spaceinvaders.client.gui.entities;

import spaceinvaders.client.gui.entities.Invader;
import spaceinvaders.client.gui.entities.InvaderBullet;
import spaceinvaders.client.gui.entities.Player;
import spaceinvaders.client.gui.entities.PlayerBullet;
import spaceinvaders.client.gui.entities.Shield;

/** Visitor. */
public interface GraphicalEntityVisitor {
  public void visit(Invader invader);

  public void visit(InvaderBullet invaderBullet);

  public void visit(Player player);

  public void visit(PlayerBullet playerBullet);

  public void visit(Shield shield);
}
