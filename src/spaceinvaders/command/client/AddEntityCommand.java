package spaceinvaders.command.client;

import spaceinvaders.client.mvc.Controller;
import spaceinvaders.command.Command;
import spaceinvaders.game.Entity;
import spaceinvaders.game.EnumEntity;
import spaceinvaders.client.gui.entities.Invader;
import spaceinvaders.client.gui.entities.Player;
import spaceinvaders.client.gui.entities.Shield;
import spaceinvaders.client.gui.entities.InvaderBullet;
import spaceinvaders.client.gui.entities.PlayerBullet;

/**
 * Add a new entity in the game.
 */
public class AddEntityCommand extends Command {
  private transient Controller executor;
  private EnumEntity type;
  private Entity body;

  public AddEntityCommand() {
    super(AddEntityCommand.class.getName());
  }

  public AddEntityCommand(EnumEntity type, Entity body) {
    this();
    this.type = type;
    this.body = body;
  }

  @Override
  public void execute() {
    switch (type) {
      case INVADER:
        executor.addEntity(Invader.class.getName(),body);
        break;
      case PLAYER:
        executor.addEntity(Player.class.getName(),body);
        break;
      case SHIELD:
        executor.addEntity(Shield.class.getName(),body);
        break;
      case INVADER_BULLET:
        executor.addEntity(InvaderBullet.class.getName(),body);
        break;
      case PLAYER_BULLET:
        executor.addEntity(PlayerBullet.class.getName(),body);
        break;
      default:
        /* do nothing */
        break;
    }
  }

  @Override
  public void setExecutor(Object executor) {
    if (executor instanceof Controller) {
      this.executor = (Controller) executor;
    }
  }

}
