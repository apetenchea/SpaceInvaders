package spaceinvaders.server.game.world;

import static spaceinvaders.game.EntityEnum.INVADER;
import static spaceinvaders.game.EntityEnum.PLAYER;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import spaceinvaders.game.EntityEnum;
import spaceinvaders.game.GameConfig;

/**
 * Builds parts of the {@link spaceinvaders.server.game.world.World}.
 */
public class ClassicWorldBuilder implements WorldBuilder {
  private final GameConfig config = GameConfig.getInstance();
  private final WorldPlan world = new World();

  @Override
  public void buildInvaders() {
    final int frameW = config.frame().getWidth();
    final int invaderW = config.invader().getWidth();
    final int invaderH = config.invader().getHeight();
    final int invaderCols = config.getInvaderCols();
    final int invaderRows = config.getInvaderRows();
    final int jumpX = invaderW + config.playerBullet().getWidth() * 3;
    final int jumpY = invaderH + invaderH / 2;
    final int witdthOffset = (frameW - invaderCols * jumpX + Math.abs(invaderW - jumpX)) / 2;
    final int heightOffset = invaderH;

    if (witdthOffset <= 0 || heightOffset <= 0) {
      // This should never happen.
      throw new AssertionError();
    }

    int offsetX = witdthOffset;
    int offsetY = heightOffset;
    List<LogicEntity> invaders = new ArrayList<>(invaderRows * invaderCols);
    for (int row = 0; row < invaderRows; ++row) {
      for (int col = 0; col < invaderCols; ++col) {
        invaders.add(new Invader(offsetX,offsetY));
        offsetX += jumpX;
      }
      offsetX = witdthOffset;
      offsetY += jumpY;
    }
    world.setEntities(INVADER,invaders);
  }

  @Override
  public void buildPlayers(List<Integer> idList) {
    if (idList == null) {
      throw new NullPointerException();
    }

    int teamSize = idList.size();
    if (teamSize <= 0) {
      // This should never happen.
      throw new AssertionError();
    }

    final int frameW = config.frame().getWidth();
    final int playerW = config.player().getWidth();
    final int jumpX = playerW * 3;
    final int witdthOffset = (frameW - (teamSize - 1) * jumpX  - teamSize * playerW) / 2;
    final int heightOffset = getPlayerHeightOffset();

    if (witdthOffset <= 0) {
      throw new AssertionError();
    }

    int offsetX = witdthOffset;
    List<LogicEntity> players = new ArrayList<>(teamSize);
    for (int player = 0; player < teamSize; ++player) {
      players.add(new HumanPlayer(idList.get(player),offsetX,heightOffset));
      offsetX += jumpX;
    }
    world.setEntities(PLAYER,players);
  }

  /**
   * {@strong Warning:} Must be called after the players have been built.
   */
  @Override
  public void buildShields() {
    Iterator<LogicEntity> it = world.getIterator(EntityEnum.PLAYER);
    if (it == null) {
      // This should never happen.
      throw new AssertionError();
    }

    final int shieldW = config.shield().getWidth();
    final int shieldH = config.shield().getHeight();
    final int playerW = config.player().getWidth();
    final int shieldsPerPlayer = config.getShieldsPerPlayer();
    final int widthOffset = playerW / 2 - shieldW / 2 - shieldW * (shieldsPerPlayer / 2);
    final int heightOffset = getPlayerHeightOffset() - shieldH * 2;

    List<LogicEntity> shields = new ArrayList<>();
    while (it.hasNext()) {
      LogicEntity player = it.next();
      int offsetX = player.getX() + widthOffset;
      for (int shield = 0; shield < shieldsPerPlayer; ++shield) {
        shields.add(new Shield(offsetX,heightOffset));
        offsetX += shieldW;
      }
    }
    world.setEntities(EntityEnum.SHIELD,shields);
  }

  @Override
  public void buildBullets() {
    world.setEntities(EntityEnum.PLAYER_BULLET,new ArrayList<LogicEntity>());
    world.setEntities(EntityEnum.INVADER_BULLET,new ArrayList<LogicEntity>());
  }

  @Override
  public World getWorld() {
    return (World) world;
  }

  private int getPlayerHeightOffset() {
    int playerH = config.player().getHeight();
    return config.frame().getHeight() - playerH - playerH / 2 - playerH / 3;
  }
}
