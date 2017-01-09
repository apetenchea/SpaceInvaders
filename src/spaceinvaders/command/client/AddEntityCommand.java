package spaceinvaders.command.client;

import java.util.List;
import spaceinvaders.client.mvc.Controller;
import spaceinvaders.client.mvc.View;
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
    String typeName;
    switch (type) {
      case INVADER:
        typeName = Invader.class.getName();
        break;
      case PLAYER:
        typeName = Player.class.getName();
        break;
      case SHIELD:
        typeName = Shield.class.getName();
        break;
      case INVADER_BULLET:
        typeName = InvaderBullet.class.getName();
        break;
      case PLAYER_BULLET:
        typeName = PlayerBullet.class.getName();
        break;
      default:
        typeName = null;
        break;
    }
    for (View view : executor.getViews()) {
      view.addEntity(typeName,body);
    }
  }

  @Override
  public void setExecutor(Object executor) {
    if (executor instanceof Controller) {
      this.executor = (Controller) executor;
    }
  }

}
