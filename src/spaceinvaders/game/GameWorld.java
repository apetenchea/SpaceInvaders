package spaceinvaders.game;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import spaceinvaders.game.GameConfig;
import spaceinvaders.utility.Couple;

/**
 * Represents the initial configuration of the game world.
 */
public class GameWorld {
  private static GameWorld singleton;

  static int entityId = 0;

  private GameConfig config;

  private Integer invadersRows;
  private Integer invadersColumns;
  private Integer invaderJump;
  private Integer invaderSpeedMilliseconds;
  private Couple<Integer,Integer> invadersOffset;
  private Couple<Integer,Integer> distanceBetweenInvaders;

  private Integer playerJump;
  private Couple<Integer,Integer> playersOffset;
  private Integer distanceBetweenPlayers;

  private Integer bulletJump;
  private Integer bulletSpeedMilliseconds;

  private List<Entity> invaders;
  private List<Entity> players;
  private List<Entity> shields;
  private Map<EnumEntity,List<Entity>> world;

  public static synchronized GameWorld getInstance() {
    if (singleton == null) {
      singleton = new GameWorld();
    }
    return singleton;
  }

  private GameWorld() {
    config = GameConfig.getInstance();

    invadersRows = 3;
    invadersColumns = 5;
    invaderJump = config.getInvaderWidth();
    invaderSpeedMilliseconds = 1000;
    int frameWidth = config.getGameFrameWidth();
    int invaderWidth = config.getInvaderWidth();
    int invadersGap = 32;
    invadersOffset = new Couple<>(
        (frameWidth - (invadersColumns * invaderWidth + invadersGap * (invadersColumns - 1))) / 2,
        32);
    distanceBetweenInvaders = new Couple<>(config.getInvaderWidth() + invadersGap,
        config.getInvaderHeight() + invadersGap);

    playerJump = config.getPlayerWidth();

    bulletJump = config.getBulletHeight();
    bulletSpeedMilliseconds = 500;
  }

  public synchronized Map<EnumEntity,List<Entity>> makeGame(List<Integer> playersId) {
    invaders = new ArrayList<>();
    shields = new ArrayList<>();
    players = new ArrayList<>();
    world = new LinkedHashMap<>();
    world.put(EnumEntity.INVADER,invaders);
    world.put(EnumEntity.PLAYER,players);
    world.put(EnumEntity.SHIELD,shields);
    addInvaders();
    int frameWidth = config.getGameFrameWidth();
    int playerWidth = config.getPlayerWidth();
    int playersGap = 128;
    switch (playersId.size()) {
      case 1:
        playersOffset = new Couple<>((frameWidth - playerWidth) / 2,600);
        distanceBetweenPlayers = 0;
        break;
      case 2:
        playersOffset = new Couple<>((frameWidth - (2 * playerWidth + playersGap)) / 2,600);
        distanceBetweenPlayers = config.getPlayerWidth() + playersGap;
        break;
      case 3:
        playersOffset = new Couple<>((frameWidth - (3 * playerWidth + 2 * playersGap)) / 2,600);
        distanceBetweenPlayers = config.getPlayerWidth() + playersGap;
        break;
    }
    Couple<Integer,Integer> pos = new Couple<Integer,Integer>(playersOffset.getFirst(),
        playersOffset.getSecond());
    for (Integer id : playersId) {
      addShield(pos);
      players.add(new Entity(id,pos));
      pos = new Couple<>(pos.getFirst() + distanceBetweenPlayers,pos.getSecond());
    }
    return world;
  }

  public int getInvaderJump() {
    return invaderJump;
  }

  public int getInvaderSpeed() {
    return invaderSpeedMilliseconds;
  }

  public int getPlayersOffsetY() {
    return playersOffset.getSecond();
  }

  public int getPlayerJump() {
    return playerJump;
  }

  public int getBulletJump() {
    return bulletJump;
  }

  public int getBulletSpeed() {
    return bulletSpeedMilliseconds;
  }

  private void addInvaders() {
    Couple<Integer,Integer> pos = new Couple<Integer,Integer>(invadersOffset.getFirst(),
        invadersOffset.getSecond());
    for (int row = 0; row < invadersRows; ++row) {
      for (int column = 0; column < invadersColumns; ++column) {
        invaders.add(new Entity(entityId++,pos));
        pos = new Couple<>(pos.getFirst() + distanceBetweenInvaders.getFirst(),pos.getSecond());
      }
      pos = new Couple<>(invadersOffset.getFirst(),
          pos.getSecond() + distanceBetweenInvaders.getSecond());
    }
  }

  private void addShield(Couple<Integer,Integer> pos) {
    Couple<Integer,Integer> shieldPos = new Couple<>(pos.getFirst() - config.getShieldWidth() / 2,
        pos.getSecond() - config.getShieldHeight() * 3);
    for (int shieldBrick = 0; shieldBrick < 3; ++shieldBrick) {
      shields.add(new Entity(entityId++,shieldPos));
      shieldPos = new Couple<>(shieldPos.getFirst() + config.getShieldWidth(),shieldPos.getSecond()); 
    }
  }
}
