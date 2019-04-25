package hu.emanuel.jeremi.antitower.entity;

import static hu.emanuel.jeremi.antitower.common.Tile64.SIZE;
import static hu.emanuel.jeremi.antitower.common.Tile64.SIZE_LOG;
import hu.emanuel.jeremi.antitower.effect.Sound;


import hu.emanuel.jeremi.antitower.entity.item.Item;
import hu.emanuel.jeremi.antitower.graphic.Graphic;
import hu.emanuel.jeremi.antitower.graphic.Graphic.WeatherType;
import hu.emanuel.jeremi.antitower.i18n.MessageProvider;
import hu.emanuel.jeremi.antitower.message.MessageHandler;
import hu.emanuel.jeremi.antitower.physics.GamePhysicsHelper;
import hu.emanuel.jeremi.antitower.save_load.TowHandler;
import hu.emanuel.jeremi.antitower.world.MapData;

public class EntityManager implements PlayerWorldConnector {

    // <editor-fold defaultstate="collapsed" desc="variables, constants">
    public MessageProvider msgProvider;

    public static final byte SPRITE = 0;
    public static final byte ITEM = 1;
    public static final byte ENEMY = 2;

    // TOW
    TowHandler saveLoadHandler;

    // FROM TOW //////////////////////////
    public int playerX, playerY;
    public int goalX, goalY;
    public MapData map;

    public ToggleDoor[] doors; // tow, NEM TESZ BELE ELEMET, CSAK INICIALIZALJA
    public Sprite[] sprites;
    public Enemy[] enemies;
    public Item[] assumables;
    int enemyStartIndex, itemStartIndex;

    public MessageHandler msgh;     // tow
    // FROM TOW //////////////////////////

    private final Player player;

    public int planeWidth, planeHeight;

    public Graphic renderer;

    private final String leveltitles[] = {"mainframe.tow", "wintertime.tow", "officemaze.tow"};
    private int levelpointer = 0;
    // </editor-fold>

    public EntityManager(Player player, int planeWidth, int planeHeight, MessageProvider rr) {

        this.player = player;

        this.msgProvider = rr;

        this.planeWidth = planeWidth;
        this.planeHeight = planeHeight;
        
        this.saveLoadHandler = new TowHandler(this);

        msgh = new MessageHandler();
    }

    public void setRenderer(Graphic renderer) {
        this.renderer = renderer;
    }

    public void setTowHandler(TowHandler saveLoadHandler) {
        this.saveLoadHandler = saveLoadHandler;
    }

    public void setWeatherAndDayTime() {
        if (renderer != null) {
            if (levelpointer == 0) {
                renderer.weather = WeatherType.RAIN;
            }
            if (levelpointer == 1) {
                renderer.weather = WeatherType.SNOW;
            }
            if (levelpointer == 2) {
                renderer.weather = WeatherType.NORMAL;
            }
        }
    }

    public void LoadLevel() {
        if (saveLoadHandler == null) {
            return;
        }

        map = new MapData(this);

        saveLoadHandler.LoadLevel("levels/" + leveltitles[levelpointer++], true);
        initSprites();

        player.x = playerX * 64;
        player.y = playerY * 64;
    }

    private void loadNextLevelOrExit() {
        if (levelpointer + 1 > leveltitles.length) {
            System.exit(0);
        }

        setWeatherAndDayTime();

        map = new MapData(this);

        saveLoadHandler.LoadLevel("levels/" + leveltitles[levelpointer++], true);
        initSprites();

        player.x = playerX * 64;
        player.y = playerY * 64;

        player.clearIntentory();

        renderer.updateMap();
    }

    public void reloadActualLevel() {
        map = new MapData(this);

        saveLoadHandler.LoadLevel("levels/" + leveltitles[levelpointer - 1], true);
        initSprites();

        player.x = playerX * 64;
        player.y = playerY * 64;

        player.clearIntentory();
    }

    public String getHelp() {
        return this.msgProvider.getHelp();
    }

    /**
     *
     */
    public void makePlayerInteractWithClosestInteractive() {
        ToggleDoor minTemp = null;
        float minDistance = Float.MAX_VALUE;
        float distance;
        for (ToggleDoor i : doors) {
            distance = GamePhysicsHelper.getDistance(i.getMapX(), i.getMapY(), player.x, player.y);
            if (distance <= minDistance) {
                minDistance = distance;
                minTemp = i;
            }
        }
        if (minDistance > 150.0) {
            player.INTERACTING = false;
            return;
        }
        if (!player.interact(minTemp)) {
            msgh.addMessage("@", msgProvider.get("need_keycard"), 1, 1);
        } else {
            msgh.addMessage("@", msgProvider.get("keycard_accepted"), 1, 1);
        }
    }

    /**
     *
     */
    private void initSprites() {
        Sprite[] temp = new Sprite[sprites.length + assumables.length + enemies.length];
        int i;
        // Phase 0
        for (i = 0; i < sprites.length; i++) {
            temp[i] = sprites[i];
        }
        // Phase 1
        itemStartIndex = sprites.length;
        for (; i < sprites.length + assumables.length; i++) {
            temp[i] = assumables[i - sprites.length].sprite;
        }
        // Phase 2
        enemyStartIndex = sprites.length + assumables.length;
        for (; i < sprites.length + assumables.length + enemies.length; i++) {
            temp[i] = enemies[i - sprites.length - assumables.length].frames;
        }
        sprites = temp;
    }

    /**
     *
     */
    public void updateDistBetweenPlayerAndSprites() {
        for (int i = 0; i < sprites.length; i++) {
            sprites[i].distanceFromPlayer
                    = GamePhysicsHelper.getDistance(sprites[i].x, sprites[i].y, player.x, player.y);
        }
        sortSpritesByDistanceAscending();
    }

    /**
     *
     */
    public void sortSpritesByDistanceAscending() {
        if (sprites.length == 0) {
            return;
        }

        Sprite temp;

        for (int i = 0; i < sprites.length - 1; i++) {
            for (int j = 0; j < sprites.length - i - 1; j++) {
                if (sprites[j].distanceFromPlayer < sprites[j + 1].distanceFromPlayer) {
                    temp = sprites[j];
                    sprites[j] = sprites[j + 1];
                    sprites[j + 1] = temp;
                }
            }
        }
    }

    /**
     *
     * @param x
     * @param y
     * @return
     */
    public boolean isThereAnEnemyThenHitIt(int x, int y) {
        if (enemies == null || enemies.length == 0) {
            return false;
        }
        for (Enemy en : enemies) {
            if (en != null && x == en.x && y == en.y) {
                en.takeDamage(10);
                if (en.isDestroyed()) {
                    msgh.addMessage("@", msgProvider.get("tower_neutralized"), 2, 1);
                    player.earnScore((en.type.ordinal() + 1) * 20);
                }
                return true;
            }
        }
        return false;
    }

    /**
     *
     * @return
     */
    public boolean attackPlayer() {
        if (enemies == null || enemies.length == 0) {
            return false;
        }

        int tempX, tempY;
        int angle;
        int sight = 200;

        for (Enemy en : enemies) {
            if (!en.destroyed) {
                tempX = en.x * SIZE + (SIZE >> 1);
                tempY = en.y * SIZE + (SIZE >> 1);

                angle = (int) (GamePhysicsHelper.rotatePlayerAngleToTarget(tempX, tempY, player.x, player.y, player.angle, planeWidth, player.FOV));

                tempX = player.x;
                tempY = player.y;

                for (int i = 0; i < sight; i++) {
                    tempX += Math.cos(GamePhysicsHelper.toCustomRad(angle, 640 * 3)) * 10;
                    tempY += Math.sin(GamePhysicsHelper.toCustomRad(angle, 640 * 3)) * 10;

                    if (map.isWall(tempX >> SIZE_LOG, tempY >> SIZE_LOG)) {
                        return false;
                    }

                    if ((int) Math.floor(tempX >> SIZE_LOG) == en.x
                            && (int) Math.floor(tempY >> SIZE_LOG) == en.y) {
                        en.gainXP();
                        en.playLaserSound();
                        player.takeDamage(en.dmg);
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public void checkGoalPoint() {
        if (goalX == (player.x >> SIZE_LOG) && goalY == (player.y >> SIZE_LOG)) {
            loadNextLevelOrExit();
        }
    }

    /**
     * Check if the player's colliding with an assumable in which case it'll
     * added to the inventory.
     */
    @Override
    public void checkAssumableCollision() {
        if (assumables.length == 0) {
            return;
        }
        Sprite[] temp;
        int l = 0;
        int minTemp = -1;
        float minDistance = Float.MAX_VALUE;
        float distance;
        for (int i = 0; i < assumables.length; i++) {
            if (assumables[i] != null) {
                distance = GamePhysicsHelper.getDistance(assumables[i].x, assumables[i].y, player.x, player.y);
                if (distance <= minDistance) {
                    minDistance = distance;
                    minTemp = i;
                }
            }
        }
        if (minDistance <= 80.0) {
            player.addItem(assumables[minTemp]);
            (new Sound("sound/door.wav")).play();
            // TODO: -1 id eset�n nullpointer exp. messagedisplayerben...megakad�lyozni
            msgh.addMessage("@", msgProvider.get("player_item_gained"), 2, 3);
            for (int j = 0; j < sprites.length; j++) {
                if (sprites[j].id == assumables[minTemp].id) {
                    temp = new Sprite[sprites.length - 1];
                    for (int k = 0; k < sprites.length; k++) {
                        if (sprites[k].id != assumables[minTemp].id) {
                            // TODO: jav�tani
                            temp[l++] = sprites[k];
                        }
                    }
                    sprites = temp;
                }
            }
            assumables[minTemp] = null;
        }
    }

    public int getItemStartIndex() {
        return itemStartIndex;
    }

    public int getEnemyStartIndex() {
        return enemyStartIndex;
    }

    @Override
    public float[] getSinTable() {
        return renderer.sinTable;
    }

    @Override
    public float[] getCosTable() {
        return renderer.cosTable;
    }

    @Override
    public boolean isOutside(int worldX, int worldY) {
        return map.isOutside(worldX, worldY);
    }

    @Override
    public boolean isCollision(int playerx, int playery, float dx, float dy) {
        return map.isPathWay((((int) (playerx + dx)) >> SIZE_LOG),
                (((int) (playery + dy)) >> SIZE_LOG));
    }

    @Override
    public Graphic g() {
        return renderer;
    }

    @Override
    public void shootTowards() {
        // TODO Auto-generated method stub

    }

    @Override
    public void setOutSide(boolean isOutside) {
        // TODO Auto-generated method stub

    }

    @Override
    public void makenteractWithClosestInteractive() {
        makePlayerInteractWithClosestInteractive();
    }

    @Override
    public void synchGraphWithData(int x, int y, int angle, int playerPaneDist) {
        renderer.setPlayerX(x);
        renderer.setPlayerY(y);
        renderer.setPlayerPaneDist(playerPaneDist);
    }

    @Override
    public void updateDistBetweenndSprites() {
        updateDistBetweenPlayerAndSprites();
    }

    @Override
    public EntityManager getManager() {
        return this;
    }

    @Override
    public void handleShoot(int x, int y, int angle) {
        GamePhysicsHelper.traceBeamTillHitsEnemy(
                100,
                (float) x, (float) y,
                renderer.cosTable, renderer.sinTable, angle,
                this
        );
    }

    @Override
    public void updateRendererPlayerReference(int x, int y, int angle, int playerPaneDist) {
        renderer.setPlayerX(x);
        renderer.setPlayerY(y);
        renderer.setAngle(angle);
        renderer.setPlayerPaneDist(playerPaneDist);
    }

}
