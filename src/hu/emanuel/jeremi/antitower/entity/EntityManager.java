package hu.emanuel.jeremi.antitower.entity;

import static hu.emanuel.jeremi.antitower.common.Tile64.SIZE;
import static hu.emanuel.jeremi.antitower.common.Tile64.SIZE_LOG;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import javax.swing.JFileChooser;

import hu.emanuel.jeremi.antitower.entity.item.AssumableItem;
import hu.emanuel.jeremi.antitower.entity.item.Item;
import hu.emanuel.jeremi.antitower.entity.item.ItemType;
import hu.emanuel.jeremi.antitower.graphic.Graphic;
import hu.emanuel.jeremi.antitower.graphic.TextureLibrary;
import hu.emanuel.jeremi.antitower.i18n.MessageProvider;
import hu.emanuel.jeremi.antitower.message.Message;
import hu.emanuel.jeremi.antitower.message.MessageHandler;
import hu.emanuel.jeremi.antitower.message.MessagePoint;
import hu.emanuel.jeremi.antitower.message.helpmessage.Help;
import hu.emanuel.jeremi.antitower.physics.GamePhysicsHelper;
import hu.emanuel.jeremi.antitower.save_load.TowHandler;
import hu.emanuel.jeremi.antitower.world.MapData;

public class EntityManager implements PlayerWorldConnector {

    // <editor-fold defaultstate="collapsed" desc="variables, constants">
    public MessageProvider msgProvider;
    public Help help;

    public static final byte SPRITE = 0;
    public static final byte ITEM = 1;
    public static final byte ENEMY = 2;

    // TOW
    private String actualLevelFileName;
    TowHandler saveLoadHandler;

    // FROM TOW //////////////////////////
    public int playerX, playerY;
    public int goalX, goalY;
    public MapData map;

    public ToggleDoor[] doors; // tow, NEM TESZ BELE ELEMET, CSAK INICIALIZALJA
    public Sprite[] sprites;
    public Enemy[] enemies;
    public AssumableItem[] assumables;
    int enemyStartIndex, itemStartIndex;

    public MessageHandler msgh;     // tow
    // FROM TOW //////////////////////////

    public ArrayList<MessagePoint> msgp;

    private final Player player;

    public int planeWidth, planeHeight;

    public Graphic renderer;

    public TextureLibrary texLib;
    // </editor-fold>

    public EntityManager(Player player, int planeWidth, int planeHeight,
            MessageProvider rr, Help h, TextureLibrary texLib, TowHandler saveLoadHandler, Graphic renderer) {
        this.saveLoadHandler = saveLoadHandler;
        this.saveLoadHandler.manager = this;

        this.player = player;

        this.texLib = texLib;

        this.msgProvider = rr;

        this.planeWidth = planeWidth;
        this.planeHeight = planeHeight;

        msgh = new MessageHandler();
        msgp = new ArrayList<>();

        this.help = h;

        this.renderer = renderer;

        LoadLevel();

        player.x = playerX * 64;
        player.y = playerY * 64;
    }

    private void LoadLevel() {

        map = new MapData(this);

        //saveLoadHandler.LoadLevel("ttest.tow", true);
        saveLoadHandler.LoadLevel("levels/test.tow", true);
        initSprites();
        
        //msgp = new ArrayList<>(Arrays.asList(new MessagePoint[]{
            //new MessagePoint("Teszt", "Bemész egy ajtón...", 10, 23, 11),}));
        msgh.addMessage("@game", "levels/test.tow loaded", -100, 2);
    }

    public BufferedImage getTexture(int id) {
        return texLib.getTexture(map.pack, id);
    }

    public BufferedImage getTexture(int id, byte type) {
        switch (type) {
            case SPRITE: {
                return texLib.getTexture(map.pack, id);
            }
            case ITEM: {
                return texLib.getItem(id);
            }
            case ENEMY: {
                return texLib.getSprite(map.pack);
            }
            default:
                return texLib.getTexture(map.pack, id);
        }
    }

    public void reloadActualLevel() {
        LoadLevel();
    }

    public void chooseLevel() {
        JFileChooser j = new JFileChooser();

        j.setCurrentDirectory(new java.io.File("."));
        j.setDialogTitle("Choose level");
        j.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        j.setAcceptAllFileFilterUsed(false);

        if (j.showOpenDialog(renderer) == JFileChooser.APPROVE_OPTION) {
            saveLoadHandler.LoadLevel(j.getSelectedFile().getPath(), false);
        } else {
            saveLoadHandler.LoadLevel("office.tow", true);
        }
    }

    public String getHelp() {
        return this.help.getHelp();
    }

    /**
     *
     */
    public void makePlayerInteractWithClosestInteractive() {
        Interactive minTemp = null;
        float minDistance = Float.MAX_VALUE;
        float distance = Float.MAX_VALUE;
        for (Interactive i : doors) {
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
        //if(minDistance < )
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
            /*
            temp[i] = new Sprite(assumables[i - sprites.length].sprite, assumables[i - sprites.length].x, assumables[i - sprites.length].y,
                    assumables[i - sprites.length].id);
             */
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
            //System.out.println("x:"+x+"|y:"+y+"|enemy x:"+en.x+"|enemy y:"+en.y);
            if (en != null && x == en.x && y == en.y) {
                en.takeDamage(10);
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
                        player.takeDamage(en.dmg);
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public void checkMessagePoint() {
        if (msgp.size() == 0) {
            return;
        }
        for (Iterator<MessagePoint> iterator = msgp.iterator(); iterator.hasNext();) {
            MessagePoint mp = iterator.next();
            if (mp.x == (player.x >> SIZE_LOG) && mp.y == (player.y >> SIZE_LOG)) {
                //System.out.println(mp.x+"|"+mp.y+"\n"+(player.x>>SIZE_LOG)+"|"+(player.y>>SIZE_LOG));
                msgh.addMessage(mp);
                iterator.remove();
            }
        }
    }

    public void checkGoalPoint() {
        if (goalX == (player.x >> SIZE_LOG) && goalY == (player.y >> SIZE_LOG)) {
            //System.out.println(mp.x+"|"+mp.y+"\n"+(player.x>>SIZE_LOG)+"|"+(player.y>>SIZE_LOG));
            System.exit(0);
        }
    }

    public void checkAssumableCollision() {
        if (assumables.length == 0) {
            return;
        }
        Sprite[] temp = null;
        int l = 0;
        for (int i = 0; i < assumables.length; i++) {
            if (assumables[i] != null) {
                if (assumables[i].x == (player.x >> SIZE_LOG) && assumables[i].y == (player.y >> SIZE_LOG)) {
                    player.addItem(assumables[i]);
                    // TODO: -1 id esetï¿½n nullpointer exp. messagedisplayerben...megakadï¿½lyozni
                    msgh.addMessage("@", msgProvider.get("player_item_gained"), 2, 3);
                    for (int j = 0; j < sprites.length; j++) {
                        if (sprites[j].id == assumables[i].id) {
                            temp = new Sprite[sprites.length - 1];
                            for (int k = 0; k < sprites.length; k++) {
                                if (sprites[k].id != assumables[i].id) {
                                    // TODO: javï¿½tani
                                    temp[l++] = sprites[k];
                                }
                            }
                            sprites = temp;
                        }
                    }
                    assumables[i] = null;
                }
            }
        }
    }

    public int getItemStartIndex() {
        return itemStartIndex;
    }

    public int getEnemyStartIndex() {
        return enemyStartIndex;
    }

    @Override
    public void synchSkyboxWithRotation(boolean left, int rotateSpeed) {
        renderer.skybox.rotate(left, rotateSpeed);
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
    public void synchGraphWithData(int x, int y, int angle, int FOV, int playerPaneDist) {
        renderer.setPlayerX(x);
        renderer.setPlayerY(y);
        renderer.setFOV(FOV);
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
    public void updateRendererPlayerReference(int x, int y, int angle, int fOV, int playerPaneDist) {
        renderer.setPlayerX(x);
        renderer.setPlayerY(y);
        renderer.setAngle(angle);
        renderer.setFOV(fOV);
        renderer.setPlayerPaneDist(playerPaneDist);
    }

}
