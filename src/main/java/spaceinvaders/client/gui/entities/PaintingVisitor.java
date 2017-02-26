package spaceinvaders.client.gui.entities;

import java.awt.Graphics;
import java.awt.image.ImageObserver;
import spaceinvaders.client.gui.entities.Invader;
import spaceinvaders.client.gui.entities.InvaderBullet;
import spaceinvaders.client.gui.entities.Player;
import spaceinvaders.client.gui.entities.PlayerBullet;
import spaceinvaders.client.gui.entities.Shield;
import spaceinvaders.game.GameConfig;

/** Paints a GraphicalEntity on the screen. */
public class PaintingVisitor implements GraphicalEntityVisitor {
  private final GameConfig config = GameConfig.getInstance();
  private final Graphics graphics;
  private final ImageObserver imgObserver;

  /**
   * @param graphics graphics context.
   * @param imgObserver update interface for notifications about drawn images.
   *
   * @throws NullPointerException if an argument is {@code null}.
   */
  public PaintingVisitor(Graphics graphics, ImageObserver imgObserver) {
    if (graphics == null || imgObserver == null) {
      throw new NullPointerException();
    }
    this.graphics = graphics;
    this.imgObserver = imgObserver;
  }

  @Override
  public void visit(Invader invader) {
    graphics.drawImage(invader.getAvatar(invader.getAvatarNumber()),invader.getX(),invader.getY(),
        config.invader().getWidth(),config.invader().getHeight(),imgObserver);
  }

  @Override
  public void visit(InvaderBullet invaderBullet) {
    graphics.drawImage(invaderBullet.getAvatar(invaderBullet.getAvatarNumber()),
        invaderBullet.getX(),invaderBullet.getY(),
        config.invaderBullet().getWidth(),config.invaderBullet().getHeight(),imgObserver);
  }

  @Override
  public void visit(Player player) {
    graphics.drawImage(player.getAvatar(player.getAvatarNumber()),player.getX(),player.getY(),
        config.player().getWidth(),config.player().getHeight(),imgObserver);
    final int nameWidth = graphics.getFontMetrics().stringWidth(player.getName());
    graphics.drawString(
        player.getName(),
        player.getX() + config.player().getWidth() / 2 - nameWidth / 2,
        player.getY() + config.player().getHeight() + config.player().getHeight() / 3);
  }

  @Override
  public void visit(PlayerBullet playerBullet) {
    graphics.drawImage(playerBullet.getAvatar(playerBullet.getAvatarNumber()),
        playerBullet.getX(),playerBullet.getY(),
        config.playerBullet().getWidth(),config.playerBullet().getHeight(),imgObserver);
  }

  @Override
  public void visit(Shield shield) {
    graphics.drawImage(shield.getAvatar(shield.getAvatarNumber()),shield.getX(),shield.getY(),
        config.shield().getWidth(),config.shield().getHeight(),imgObserver);
  }
}
