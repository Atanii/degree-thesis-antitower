package hu.emanuel.jeremi.antitower.graphic;

import hu.emanuel.jeremi.antitower.Game.GameState;
import static hu.emanuel.jeremi.antitower.common.Tile64.*;
import static hu.emanuel.jeremi.antitower.graphic.ImageAndPostProcessHelper.*;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import hu.emanuel.jeremi.antitower.effect.Rain;
import hu.emanuel.jeremi.antitower.effect.Snow;
import hu.emanuel.jeremi.antitower.effect.Weather;
import hu.emanuel.jeremi.antitower.entity.EntityManager;
import hu.emanuel.jeremi.antitower.entity.Player;
import hu.emanuel.jeremi.antitower.message.MessageDisplayer;
import hu.emanuel.jeremi.antitower.physics.GamePhysicsHelper;
import hu.emanuel.jeremi.antitower.world.MapData;
import hu.emanuel.jeremi.antitower.world.Skybox;
import java.awt.image.DataBufferInt;
import java.util.Random;

public class Graphic extends JPanel {

    /**
     *
     */
    private static final long serialVersionUID = 3697273593000989014L;

    // <editor-fold defaultstate="collapsed" desc="fields">
    /////////////////////////////// PLANE ////////////////////////////////////////////////////
    private int planeWidth, planeHeight;
    //////////////////////////////////////////////////////////////////////////////////////////

    /////////////////////////////// GRAPHICS FOR DRAWING ON SCREEN ///////////////////////////
    private Graphics gb;			// for the graphics buffer and drawing
    private BufferedImage frame;	// image for the actual frame
    private int[] output;
    //////////////////////////////////////////////////////////////////////////////////////////

    /////////////////////////////// ANGLES DEPENDING ON THE PLANEWIDTH ///////////////////////
    public int ANGLE0 = 0;
    public int ANGLE60 = planeWidth;
    public int ANGLE30 = ANGLE60 / 2;
    public int ANGLE360 = ANGLE60 * 6;
    public int ANGLE5 = ANGLE30 / 6;
    public int ANGLE90 = ANGLE30 * 3;
    public int ANGLE180 = ANGLE90 * 2;
    public int ANGLE270 = ANGLE90 * 3;
    public int angleBetweenRays;
    //////////////////////////////////////////////////////////////////////////////////////////

    /////////////////////////////// TABLES FOR PRECALCULATED VALUES //////////////////////////
    public float sinTable[];
    public float InvSinTable[];
    public float cosTable[];
    public float InvCosTable[];
    public float tanTable[];
    public float InvTanTable[];
    public float xStepTable[];
    public float yStepTable[];
    public float fishEyeCorrectionTable[];
    //////////////////////////////////////////////////////////////////////////////////////////

    /////////////////////////////// SPRITE CASTING ///////////////////////////////////////////
    float zbuffer[];
    //////////////////////////////////////////////////////////////////////////////////////////

    /////////////////////////////// PLAYER LOCAL DATA ////////////////////////////////////////
    private Player player;
    private int FOV;
    private int playerPaneDist;
    private int playerX, playerY;
    private int angle;
    //////////////////////////////////////////////////////////////////////////////////////////

    /////////////////////////////// FLAGS ////////////////////////////////////////////////////
    public boolean IS_ANGLE_MARKER_ON = false;
    public boolean IS_SHADERS_ON = false;
    public boolean IS_SKYBOX_ON = true;
    public boolean IS_RAIN_ON = false;
    public boolean IS_SPRITES_ON = true;
    public boolean IS_HELP_ON = false;
    public boolean IS_STATUS_ON = false;
    public boolean IS_RENDER_OVERHEAD_AND_HUD = true;
    //////////////////////////////////////////////////////////////////////////////////////////
    // For program constructor:
    int screenw, screenh;
    /////////////////////////////// map LOCAL DATA ///////////////////////////////////////////
    private int mapWidth;
    private int mapHeight;
    //////////////////////////////////////////////////////////////////////////////////////////

    // MANAGER AND LIBRARY OBJECTS: EntityManager, MapData, TextureLibrary, MessageDisplayers
    EntityManager manager;
    MapData map;
    TextureLibrary tex;
    MessageDisplayer msgdisp;
    GameState mode;
        
    // WEATHER
    public enum WeatherType { NORMAL, SNOW, RAIN };
    public WeatherType weather;
    private Weather snow;
    private Weather rain;

    // OBJECTS FOR EFFECTS: SKYBOX, RAIN
    public Skybox skybox;
    

    private final RayCastedGridData rc = new RayCastedGridData();
    // </editor-fold>

    /**
     * Precalculate values for the tables (sinTable, cosTable, ...) and fills
     * them.
     */
    private void preCalculateTablesAndValues() {
        // ANGLES BASED ON THE PLANEWIDTH
        ANGLE0 = 0;
        ANGLE60 = planeWidth;
        ANGLE30 = ANGLE60 / 2;
        ANGLE360 = ANGLE60 * 6;
        ANGLE5 = ANGLE30 / 6;
        ANGLE90 = ANGLE30 * 3;
        ANGLE180 = ANGLE90 * 2;
        ANGLE270 = ANGLE90 * 3;

        // DECLARING PRECALCULATED TABLES (sin,cos,...etc)
        sinTable = new float[ANGLE360 + 1];
        InvSinTable = new float[ANGLE360 + 1];
        cosTable = new float[ANGLE360 + 1];
        InvCosTable = new float[ANGLE360 + 1];
        tanTable = new float[ANGLE360 + 1];
        InvTanTable = new float[ANGLE360 + 1];
        xStepTable = new float[ANGLE360 + 1];
        yStepTable = new float[ANGLE360 + 1];
        // fisheye correction table
        fishEyeCorrectionTable = new float[ANGLE60 + 1];

        // FILLING PRECALCULATED TABLES
        for (int i = 0; i <= ANGLE360; i++) {
            sinTable[i] = (float) Math.sin(GamePhysicsHelper.toCustomRad((float) i, ANGLE180));
            InvSinTable[i] = 1.0f / sinTable[i];
            cosTable[i] = (float) Math.cos(GamePhysicsHelper.toCustomRad((float) i, ANGLE180));
            InvCosTable[i] = 1.0f / cosTable[i];
            tanTable[i] = (float) Math.tan(GamePhysicsHelper.toCustomRad((float) i, ANGLE180));
            InvTanTable[i] = 1.0f / tanTable[i];
            // Facing left:
            if (i >= ANGLE90 && i < ANGLE270) {
                xStepTable[i] = SIZE / tanTable[i];
                xStepTable[i] = xStepTable[i] > 0 ? -xStepTable[i] : xStepTable[i];
            } else {
                xStepTable[i] = SIZE / tanTable[i];
                xStepTable[i] = xStepTable[i] < 0 ? -xStepTable[i] : xStepTable[i];
            }

            // Facing down:
            if (i >= ANGLE0 && i < ANGLE180) {
                yStepTable[i] = SIZE * tanTable[i];
                yStepTable[i] = yStepTable[i] < 0 ? -yStepTable[i] : yStepTable[i];
            } else {
                yStepTable[i] = SIZE * tanTable[i];
                yStepTable[i] = yStepTable[i] > 0 ? -yStepTable[i] : yStepTable[i];
            }
        }
        // FILLING FISHEYE CORRECTION TABLE
        for (int i = -ANGLE30; i <= ANGLE30; i++) {
            fishEyeCorrectionTable[i + ANGLE30] = (float) (1.0f / Math.cos(GamePhysicsHelper.toCustomRad((float) i, ANGLE180)));
        }
    }

    /**
     * Since
     */
    private void setupPlayer() {
        // TODO: get rid of this
        player.FOV = ANGLE60;
        setFOV(player.FOV);
        player.speed = 5;
        //player.rotateSpeed = ANGLE5 / 2;
        player.rotateSpeed = ANGLE5;
        player.playerPaneDist = (int) ((planeWidth >> 1) / (float) Math.tan(GamePhysicsHelper.toCustomRad(player.FOV >> 1, ANGLE180)));
        player.isInside = false;
        player.angle = ANGLE0;
        angleBetweenRays = getFOV() / planeWidth;
    }

    /**
     *
     */
    private void initBuffersForSpriteCasting() {
        zbuffer = new float[planeWidth];
    }

    /**
     * Setup: jframe and gameField It calls the function for filling the
     * sinTable, cosTable, ...etc. Hide cursor by make it equal with the
     * transparentCursor. It starts the gameThread.
     */
    public Graphic(int planeWidth, int planeHeight, Player player, EntityManager manager) {
        super();
        
        // GAME STATE
        mode = GameState.MENU;

        // EntityManager, TextureLibrary, Map
        this.manager = manager;
        this.tex = manager.texLib;

        // Frame initialization:
        frame = new BufferedImage(planeWidth, planeHeight, BufferedImage.TYPE_INT_ARGB);
        output = ( (DataBufferInt) (frame.getRaster().getDataBuffer()) ).getData();
        this.setIgnoreRepaint(true);

        // Screen data:
        this.screenw = planeWidth;
        this.screenh = planeHeight;
        this.planeWidth = planeWidth;
        this.planeHeight = planeHeight;
        
        // Player:
        this.player = player;

        // Weather:
        weather = WeatherType.NORMAL;
        snow = Weather.getSnow(planeWidth, planeHeight);
        snow.generate();
        rain = Weather.getRain(planeWidth, planeHeight);
        rain.generate();
        
        preCalculateTablesAndValues();
        setupPlayer();
        initBuffersForSpriteCasting();

        msgdisp = new MessageDisplayer(manager.msgh);

        skybox = new Skybox(player.FOV, tex.loadAndGetTextureFromImageFile("textures/skybox_2560x240.png", 656345));
    }
    
    public void updateMap() {
        this.map = manager.map;
        mapWidth = map.width;
        mapHeight = map.height;
    }

    public void takeScreenshot() {
        File outputfile = new File("C:\\screenshot_" + System.currentTimeMillis() + "_.png");
        try {
            ImageIO.write(frame, "png", outputfile);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * Set the resolution for rendering and displaying.
     *
     * @param w
     * @param h
     */
    public void setResolution(int w, int h) {
        // Display resolution:
        this.screenw = w;
        this.screenh = h;
        // Render resolution is slower for better perfomance:
        this.planeWidth = w >> 2;
        this.planeHeight = h >> 2;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        //g.drawImage(ImageAndPostProcessHelper.scaleNearest(frame, 4), 0, 0, this);        
        // draw current frame
        
        if(mode == GameState.MENU) {
            //System.out.println(planeWidth + 'x' + planeHeight);
            g.drawImage(manager.getMenuImage(), 0, 0, this);
        } else {
            g.drawImage(frame, 0, 0, this);        
            // draw messages currently waiting for drawing
            drawMessages(g);
            // if the player is inside and rain is turn on, then it renders simple rain effect
            if (weather == WeatherType.SNOW && !player.isInside) {
                snow.render(g, player.angle);
            }
            if (weather == WeatherType.RAIN && !player.isInside) {
                rain.render(g, player.angle);
            }
            // write help information about the controls
            if (IS_HELP_ON) {
                drawHelp(g);
            }
            // show player data
            if (IS_STATUS_ON) {
                showPlayerData(g);
            }
        }
    }
    
    public void updateWeather(long delta) {
        if(weather == weather.NORMAL) {
            return;
        }
        else if(weather == weather.RAIN) {
            rain.update(delta);
        }
        else if(weather == weather.SNOW) {
            snow.update(delta);
        }
    }
    
    /**
     * Draw the current message.
     * @param g 
     */
    private void drawMessages(Graphics g) {
        if( msgdisp != null ) {
            g.setColor(Color.blue);
            g.drawString(msgdisp.getMessage(), 10, 20);
        }
    }
    
    public void setState(GameState mode) {
        this.mode = mode;
    }

    /////////////////////////////// RAY-CASTING //////////////////////////////////////////////
    /**
     * Renders the overhead graphic ie. HUD, perspective image of the actual
     * item...etc.
     *
     */
    public final void renderHUDAndOverheadGraphic() {
        // Check if player hasn't even selected a weapon:
        if(player.getSelectedItem() == null) {
            return;
        }
        // If there is a selected weapon, then paint it's overhead image on the hud:
        int[] img = new int[64 * 64];
        tex.getItem(player.getSelectedItem().overheadImg).getRGB(0, 0, 64, 64, img, 0, 64);
        for(int y = screenh - SIZE; y < screenh; y++) {
            for(int x = (screenw >> 1) - (SIZE >> 1); x < ((screenw >> 1) + (SIZE >> 1)); x++) {
                int index = y * screenw + x;
                int imageX = x - ( (screenw >> 1) - (SIZE >> 1) );
                int imageY = y - ( screenh - SIZE );
                int imageIndex = imageY * SIZE + imageX; // (y - screenh - (SIZE << 1)) * (SIZE >> 1) + (x - ((screenw >> 1) - SIZE));
                if(img[imageIndex] != 0)
                    output[index] = img[imageIndex];
            }
        }
    }

    /**
     * Draws the beam of the weapon handled by the player. It's always
     * positioned to the center of the screen.
     *
     */
    public final void renderBeam() {
        int x = planeWidth >> 1;
        for(int y = (planeHeight >> 1); y < planeHeight; y++) {
            output[y * planeWidth + x] = Color.BLUE.getRGB();
        }
    }

    /**
     * This class represents all of the data of the map-cell being hit by a ray.
     */
    private class RayCastedGridData {
        // WALL
        int offset;													// texture offset
        float wallDistHorizontal, wallDistVertical, distance = .0f;	// Horizontal and vertical wall distances:
        float Xa, Ya;												// moving horizontally (vertically) on grid
        float Ay, Ax;												// wall intersection coordinates
        float tempAx = .0f, tempAy = .0f;							// wall-slice coordinate for computing texture offset
        int projectedSliceHeight;									// this height what you'll see
        int gridX, gridY;											// INTEGER grid coordinates for the intersections		
        float horizontalHeight;
        float verticalHeight;
        // arrays for the texture pixels and the slice pixels
        // all of the textures has the size of SIZE
        int pixels[] = new int[SIZE];
        int slicePixels[];

        boolean isClosedDoor = true;

        // SPRITES		
        int tileIndexHor = 0, tileIndexVer = 0;
        boolean DOWN, RIGHT;

        // THIN WALLS
        float thinWallTempHor[] = new float[3];
        float thinWallTempVer[] = new float[3];
        boolean isThereThinWallHor, isThereThinWallVer = false;
        boolean isHorizontalWallInside = false, isVerticalWallInside = false;

        int startDraw;
        int v_startDraw;

        public void hardReset() {
            distance = .0f;										// Horizontal and vertical wall distances:
            tempAx = .0f;										// wall-slice coordinate for computing texture offset 
            tempAy = .0f;

            // arrays for the texture pixels and the slice pixels
            // all of the textures has the size of SIZE
            pixels = new int[SIZE];

            // spritecasting		
            tileIndexHor = 0;
            tileIndexVer = 0;

            // thin walls
            thinWallTempHor = new float[3];
            thinWallTempVer = new float[3];
            isThereThinWallHor = isThereThinWallVer = false;
            isHorizontalWallInside = isVerticalWallInside = false;
        }

        public void reset() {
            // no thin-wall is the default
            isThereThinWallHor = false;
            isThereThinWallVer = false;
            thinWallTempHor[0] = Float.MAX_VALUE;
            thinWallTempVer[0] = Float.MAX_VALUE;

            isClosedDoor = false;

            // default wall height
            verticalHeight = horizontalHeight = SIZE;

            // default state is inside
            isHorizontalWallInside = true;
            isVerticalWallInside = true;

            v_startDraw = startDraw = Integer.MAX_VALUE;
        }
    }

    /**
     * The most important function of the ray-casting engine. It casts rays from
     * the position of the player towards the map-cells in the range of the
     * screen (FOV - Field Of View, possibly 60?) until the ray hits a wall. If
     * the ray hit a wall, then depending on the player - wall coordinate
     * distance this function determine the height, texture offset, screen
     * position of the specific wall slice and draws it into the screenbuffer.
     *
     * The function starts at the leftmost coloumn of the screen (0?) and ends
     * at the rightmost (60?).
     *
     * When all rays are casted, the screenbuffer will be drawn onto screen.
     */
    public final void render() {

        rc.hardReset();

        // The angle of the angle to be casted:
        int rayAngle = (rayAngle = getAngle() - ANGLE30) < 0 ? rayAngle + ANGLE360 : rayAngle;

        // Loop from left to right
        for (int raysCasted = 0; raysCasted < planeWidth; raysCasted += angleBetweenRays) {

            // START VALUES FOR VARIABLES, DISTANCES...ETC
            // default distance
            zbuffer[raysCasted] = Float.MAX_VALUE;

            rc.reset();

            // <editor-fold defaultstate="collapsed" desc="HORIZONTAL & VERTICAL INTERSECTIONS">
            ///////////////	HORIZONTAL INTERSECTIONS /////////////////////////////////////////
            if ((rayAngle > ANGLE0) && (rayAngle < ANGLE180)) {	// facing down

                // angle > ANGLE 0 && angle < ANGLE 180
                //System.out.println("You're facing down!");
                rc.DOWN = true;
                rc.Ya = SIZE;
                rc.Ay = ((getPlayerY() >> SIZE_LOG) << SIZE_LOG) + SIZE;
                rc.Ax = getPlayerX() + (InvTanTable[rayAngle] * (rc.Ay - getPlayerY()));
            } else {
                // ! (angle > ANGLE 0 && angle < ANGLE 180)
                //System.out.println("You're facing up!");
                rc.DOWN = false;
                rc.Ya = -SIZE;
                rc.Ay = (((getPlayerY() >> SIZE_LOG))) << SIZE_LOG;
                rc.Ax = getPlayerX() + (InvTanTable[rayAngle] * (--rc.Ay - getPlayerY()));
                //Ay -= 1.0;
            }
            rc.Xa = xStepTable[rayAngle];
            while (true) {
                // GRID COORDINATES (ARRAY COORDINATE)
                rc.gridX = (int) Math.floor(rc.Ax / SIZE);
                rc.gridY = (int) Math.floor(rc.Ay / SIZE);
                // CHECK ANGLE
                if ((rayAngle == ANGLE0) ^ (rayAngle == ANGLE180)) {
                    rc.wallDistHorizontal = Float.MAX_VALUE;
                    break;
                }
                // OUT OF GRID ?
                if (((rc.gridX < 0) ^ (rc.gridX >= mapWidth)) || ((rc.gridY < 0) ^ (rc.gridY >= mapHeight))) {
                    rc.wallDistHorizontal = Float.MAX_VALUE;
                    break;
                }
                // THIN WALL
                if (!rc.isThereThinWallHor && map.isDoor(rc.gridX, rc.gridY)) {
                    if (map.isOpen(rc.gridX, rc.gridY)) {
                        rc.thinWallTempHor[0] = Math.abs((rc.Ax - getPlayerX()) * InvCosTable[rayAngle]);
                        rc.thinWallTempHor[1] = DOOR_OPENED;
                        rc.thinWallTempHor[2] = (float) Math.floor(rc.Ax);
                        rc.isThereThinWallHor = true;
                    } else {
                        rc.isClosedDoor = true;
                        rc.wallDistHorizontal = Math.abs((rc.Ax - getPlayerX()) * InvCosTable[rayAngle]);
                        rc.tileIndexHor = DOOR_CLOSED;
                        rc.tempAx = (float) Math.floor(rc.Ax);
                        zbuffer[raysCasted] = (float) Math.sqrt((getPlayerX() - ((rc.gridX << SIZE_LOG) + (SIZE >> 1))) * (getPlayerX() - ((rc.gridX << SIZE_LOG) + (SIZE >> 1)))
                                + (getPlayerY() - ((rc.gridY << SIZE_LOG) + (SIZE >> 1))) * (getPlayerY() - ((rc.gridY << SIZE_LOG) + (SIZE >> 1))));
                        break;
                    }
                }
                // NORMAL WALL
                if (map.isWall(rc.gridX, rc.gridY)) {

                    if (map.isOutside(rc.gridX, rc.gridY)) {
                        rc.isHorizontalWallInside = false;
                    }

                    rc.wallDistHorizontal = Math.abs((rc.Ax - getPlayerX()) * InvCosTable[rayAngle]);
                    rc.tileIndexHor = map.texMap[rc.gridY * mapWidth + rc.gridX];
                    rc.tempAx = (float) Math.floor(rc.Ax);
                    zbuffer[raysCasted] = (float) Math.sqrt((getPlayerX() - ((rc.gridX << SIZE_LOG) + (SIZE >> 1))) * (getPlayerX() - ((rc.gridX << SIZE_LOG) + (SIZE >> 1)))
                            + (getPlayerY() - ((rc.gridY << SIZE_LOG) + (SIZE >> 1))) * (getPlayerY() - ((rc.gridY << SIZE_LOG) + (SIZE >> 1))));

                    rc.horizontalHeight = map.getHeight(rc.gridX, rc.gridY);

                    break;
                }
                // NEXT STEP
                rc.Ax += rc.Xa;
                rc.Ay += rc.Ya;
            } // while(true)
            //////////////////////////////////////////////////////////////////////////////////

            ///////////////	VERTICAL INTERSECTIONS ///////////////////////////////////////////
            if ((rayAngle < ANGLE90) ^ (rayAngle > ANGLE270)) {	// facing right
                rc.RIGHT = true;
                rc.Xa = SIZE;
                rc.Ax = ((getPlayerX() >> SIZE_LOG) << SIZE_LOG) + SIZE;
                rc.Ay = getPlayerY() + (tanTable[rayAngle] * (rc.Ax - getPlayerX()));
            } else {												// facing left
                rc.RIGHT = false;
                rc.Xa = -SIZE;
                rc.Ax = ((getPlayerX() >> SIZE_LOG) << SIZE_LOG);
                rc.Ay = getPlayerY() + (tanTable[rayAngle] * (--rc.Ax - getPlayerX()));
                //Ax -= 1.0;
            }
            rc.Ya = yStepTable[rayAngle];
            while (true) {
                // GRID COORDINATES (ARRAY COORDINATE)
                rc.gridX = (int) Math.floor(rc.Ax / SIZE);
                rc.gridY = (int) Math.floor(rc.Ay / SIZE);
                // CHECK ANGLE
                if ((rayAngle == ANGLE90) ^ (rayAngle == ANGLE270)) {
                    rc.wallDistVertical = Float.MAX_VALUE;
                    break;
                }
                // OUT OF GRID ?
                if (((rc.gridX < 0) ^ (rc.gridX >= mapWidth)) || ((rc.gridY < 0) ^ (rc.gridY >= mapHeight))) {
                    rc.wallDistVertical = Float.MAX_VALUE;
                    break;
                }
                // THIN WALL
                if (!rc.isThereThinWallVer && map.isDoor(rc.gridX, rc.gridY)) {
                    if (map.isOpen(rc.gridX, rc.gridY)) {
                        rc.thinWallTempVer[0] = Math.abs((rc.Ay - getPlayerY()) * InvSinTable[rayAngle]);
                        rc.thinWallTempVer[1] = DOOR_OPENED;
                        rc.thinWallTempVer[2] = rc.Ay;
                        rc.isThereThinWallVer = true;
                    } else {
                        rc.isClosedDoor = true;
                        rc.wallDistVertical = Math.abs((rc.Ay - getPlayerY()) * InvSinTable[rayAngle]);
                        rc.tileIndexVer = DOOR_CLOSED;
                        rc.tempAy = rc.Ay;
                        zbuffer[raysCasted] = (float) Math.sqrt((getPlayerX() - ((rc.gridX << SIZE_LOG) + (SIZE >> 1))) * (getPlayerX() - ((rc.gridX << SIZE_LOG) + (SIZE >> 1)))
                                + (getPlayerY() - ((rc.gridY << SIZE_LOG) + (SIZE >> 1))) * (getPlayerY() - ((rc.gridY << SIZE_LOG) + (SIZE >> 1))));
                        break;
                    }
                }
                // NORMAL WALL
                if (map.isWall(rc.gridX, rc.gridY)) {

                    if (map.isOutside(rc.gridX, rc.gridY)) {
                        rc.isVerticalWallInside = false;
                    }

                    rc.wallDistVertical = Math.abs((rc.Ay - getPlayerY()) * InvSinTable[rayAngle]);
                    rc.tileIndexVer = map.texMap[rc.gridY * mapWidth + rc.gridX];
                    rc.tempAy = rc.Ay;

                    rc.verticalHeight = map.getHeight(rc.gridX, rc.gridY);

                    break;
                }
                // NEXT STEP
                rc.Ay += rc.Ya;
                rc.Ax += rc.Xa;
            } // while(true)			
            //////////////////////////////////////////////////////////////////////////////////
            // </editor-fold>

            ///////////////	TEXTURE PIXEL OFFSET /////////////////////////////////////////////
            // HORIZONTAL
            if (rc.wallDistHorizontal < rc.wallDistVertical) {
                rc.distance = rc.wallDistHorizontal / fishEyeCorrectionTable[raysCasted];
                zbuffer[raysCasted] = rc.distance;
                rc.offset = 63 - (((int) rc.tempAx) & 63);
                    manager.getTexture(rc.tileIndexHor).getRGB(rc.offset, 0, 1, SIZE, rc.pixels, 0, 1);
                    /*
                if (rc.DOWN) {
                    rc.offset = 63 - (((int) rc.tempAx) & 63);
                    manager.getTexture(rc.tileIndexHor).getRGB(rc.offset, 0, 1, SIZE, rc.pixels, 0, 1);
                } else {
                    rc.offset = (((int) rc.tempAx) & 63);
                    manager.getTexture(rc.tileIndexHor).getRGB(rc.offset, 0, 1, SIZE, rc.pixels, 0, 1);
                }
                    */
                // VERTICAL
            } else {
                rc.isHorizontalWallInside = rc.isVerticalWallInside;
                rc.horizontalHeight = rc.verticalHeight;
                rc.distance = rc.wallDistVertical / fishEyeCorrectionTable[raysCasted];
                zbuffer[raysCasted] = rc.distance;
                if (!rc.RIGHT) {
                    rc.offset = 63 - (((int) rc.tempAy) & 63);
                    manager.getTexture(rc.tileIndexVer).getRGB(rc.offset, 0, 1, SIZE, rc.pixels, 0, 1);
                } else {
                    rc.offset = (((int) rc.tempAy) & 63);
                    manager.getTexture(rc.tileIndexVer).getRGB(rc.offset, 0, 1, SIZE, rc.pixels, 0, 1);
                }
            }
            // TODO: ezt elt?ntetni
            float shaderDistance = rc.distance;

            ///////////////	WALL DISTANCE AND STARTDRAW VALUE ////////////////////////////////
            rc.projectedSliceHeight = (int) ((getPlayerPaneDist() * rc.horizontalHeight) / rc.distance);

            // "smallest" wall size must be zero
            if (rc.projectedSliceHeight < 0) {
                rc.projectedSliceHeight = 0;
            }

            // startDraw is the point on the screen where the wall-drawing starts
            rc.startDraw = ((planeHeight - rc.projectedSliceHeight) >> 1);
            if (rc.startDraw >= planeHeight) {
                rc.startDraw = planeHeight - 1;
            }
            //////////////////////////////////////////////////////////////////////////////////

            // <editor-fold defaultstate="collapsed" desc="FLOOR & CEIL CASTING">
            /////////////// FLOOR-CASTING ////////////////////////////////////////////////////			
            // 90%-k?t magam csin?ltam meg a matek alapj?n, marad?k 10%-?k (indexel?s ?s angle+rayAngle) innen van: 
            // https://www.allegro.cc/forums/thread/374305
            // Nem m?k?dtek a megold?sok, ez?rt egyszer?s?tettem rajtuk.
            // Stack postom: https://gamedev.stackexchange.com/questions/159285/ray-casting-floor-casting-part-fails			 
            int floorCastingStartPixel = rc.startDraw + rc.projectedSliceHeight;
            int x, y;
            if (floorCastingStartPixel < planeHeight) {
                for (int i = floorCastingStartPixel; i <= planeHeight - 1; i++) {
                    // floor distance
                    rc.distance = ((float) (((float) PLAYERHEIGHT
                            / (i - (planeHeight >> 1))) * getPlayerPaneDist())) * fishEyeCorrectionTable[raysCasted];

                    // floor-tile coordinate in the world
                    x = ((int) (rc.distance * (cosTable[rayAngle]))) + getPlayerX();
                    y = ((int) (rc.distance * (sinTable[rayAngle]))) + getPlayerY();

                    // floor-tile coordinate on the grid
                    int mapX = x >> 6;
                    int mapY = y >> 6;

                    // check if out of grid
                    if (mapX < 0 || mapY < 0 || mapX >= mapWidth || mapY >= mapHeight) {
                        continue;
                    }

                    // outside or inside
                    // x & 63 = x % 64 and x & 63 = x % 64, texture offset
                    // TODO: x&63 helyett x&(SIZE-1)-el ekvivalens ?ltal?nos?t?s
                    if (!map.isOutside(mapX, mapY)) {
                        if (IS_SHADERS_ON) {
                           output[raysCasted + i * planeWidth] = addFogEffect(manager.getTexture(map.texMap[mapY * mapWidth + mapX]).getRGB(x & 63, y & 63), rc.distance);
                        } else {
                            //screenBuffer.setRGB(raysCasted, i, manager.getTexture(map.texMap[mapY * mapWidth + mapX]).getRGB(x & 63, y & 63));
                            output[raysCasted + i * planeWidth] = manager.getTexture(map.texMap[mapY * mapWidth + mapX]).getRGB(x & 63, y & 63);
                        }
                    } else {
                        output[raysCasted + i * planeWidth] = addFogEffect(manager.getTexture(map.texMap[mapY * mapWidth + mapX]).getRGB(x & 63, y & 63), rc.distance);
                        //screenBuffer.setRGB(raysCasted, i, manager.getTexture(map.texMap[mapY * mapWidth + mapX]).getRGB(x & 63, y & 63));
                        //output[raysCasted + i * planeWidth] = manager.getTexture(map.texMap[mapY * mapWidth + mapX]).getRGB(x & 63, y & 63);
                    }

                }
            }
            //////////////////////////////////////////////////////////////////////////////////

            /////////////// CEIL-CASTING ////////////////////////////////////////////////////
            if (rc.startDraw >= 0) {
                for (int i = 0; i <= rc.startDraw; i++) {
                    // ceil distance
                    rc.distance = (float) (Math.abs(((float) PLAYERHEIGHT
                            / (i - (planeHeight >> 1))) * getPlayerPaneDist())) * fishEyeCorrectionTable[raysCasted];

                    // ceil world coordinates
                    x = ((int) (rc.distance * (cosTable[rayAngle]))) + getPlayerX();
                    y = ((int) (rc.distance * (sinTable[rayAngle]))) + getPlayerY();

                    // ceil grid coordinates
                    int mapX = x >> 6;
                    int mapY = y >> 6;

                    // check if outside of grid
                    if (mapX < 0 || mapY < 0 || mapX >= mapWidth || mapY >= mapHeight) {
                        continue;
                    }

                    // outside or inside
                    // x & 63 = x % 64 and x & 63 = x % 64, texture offset
                    // TODO: x&63 helyett x&(SIZE-1)-el ekvivalens ?ltal?nos?t?s
                    // TODO: megjav?tani
                    if (!map.isOutside(mapX, mapY)) {
                        if (IS_SHADERS_ON) {
                            //screenBuffer.setRGB(raysCasted, i, addFogEffect(manager.getTexture(map.ceiling).getRGB(x & 63, y & 63), rc.distance));
                            output[raysCasted + i * planeWidth] = addFogEffect(manager.getTexture(map.ceiling).getRGB(x & 63, y & 63), rc.distance);
                        } else {
                            //screenBuffer.setRGB(raysCasted, i, manager.getTexture(map.ceiling).getRGB(x & 63, y & 63));
                            output[raysCasted + i * planeWidth] = addFogEffect(manager.getTexture(map.ceiling).getRGB(x & 63, y & 63), rc.distance);
                            //output[raysCasted + i * planeWidth] = manager.getTexture(map.ceiling).getRGB(x & 63, y & 63);
                        }
                    }
                }
            }
            //////////////////////////////////////////////////////////////////////////////////
            // </editor-fold>

            ///////////////	SCALING TEXTURE SLICE ////////////////////////////////////////////
            int y_ratio;
            // Scaling function simplified to handle one pixel wide images better. Inlined for better perfomance.
            if (rc.projectedSliceHeight == 0) {
                rc.projectedSliceHeight = 1;
            }
            rc.slicePixels = new int[rc.projectedSliceHeight];
            y_ratio = (int) ((SIZE << 16) / rc.projectedSliceHeight) + 1;
            for (int i = 0; i < rc.projectedSliceHeight; i++) {
                if (((i * y_ratio) >> 16) < SIZE) {
                    // wall
                    rc.slicePixels[i] = rc.pixels[((i * y_ratio) >> 16)];
                }
            }
            //////////////////////////////////////////////////////////////////////////////////
            
            ///////////////	DRAWING WALL SLICE ///////////////////////////////////////////////
            // <editor-fold defaultstate="collapsed" desc="drawing wall">
            rc.slicePixels = addFogEffect(rc.slicePixels, shaderDistance);
            if (!rc.isClosedDoor && rc.isHorizontalWallInside && IS_SHADERS_ON) {
                //rc.slicePixels = addFogEffect(rc.slicePixels, shaderDistance);
            }
            for (int wy = rc.startDraw, index = 0;
                    wy <= rc.startDraw + rc.projectedSliceHeight - 1; wy++, index++) {
                if (wy >= 0 && wy < planeHeight) {
                    if (IS_ANGLE_MARKER_ON && rayAngle == (player.angle)) {
                        //screenBuffer.setRGB(raysCasted, wy, 0xffffff);
                        output[raysCasted + wy * planeWidth] = 0xffffff;
                    } else {
                        //screenBuffer.setRGB(raysCasted, wy, rc.slicePixels[index]);
                        output[raysCasted + wy * planeWidth] = rc.slicePixels[index];
                    }

                }
            }
            // </editor-fold>
            //////////////////////////////////////////////////////////////////////////////////
            
            ///////////////	SCALING AND DRAWING THIN WALL ////////////////////////////////////
            // <editor-fold defaultstate="collapsed" desc="thin wall">
            /*
                thinWallTemp...[0] =  DISTANCE FROM PLAYER // Math.abs( (Ax-playerX)*InvCosTable[rayAngle] );
                thinWallTemp...[1] =  TILE TEXTURE INDEX   // DOOR_OPENED;
                thinWallTemp...[2] =  AX or AY             // (float) Math.floor(Ax);	// tempAx
             */
            if (rc.isThereThinWallVer || rc.isThereThinWallHor) {
                if (rc.thinWallTempHor[0] < rc.thinWallTempVer[0]) {
                    rc.distance = (int) rc.thinWallTempHor[0] / fishEyeCorrectionTable[raysCasted];

                    if (rc.DOWN) {
                        rc.offset = 63 - (((int) rc.thinWallTempHor[2]) & 63);
                    } else {
                        rc.offset = (((int) rc.thinWallTempHor[2]) & 63);
                    }

                } else {
                    rc.distance = (int) rc.thinWallTempVer[0] / fishEyeCorrectionTable[raysCasted];

                    if (!rc.RIGHT) {
                        rc.offset = 63 - (((int) rc.thinWallTempVer[2]) & 63);
                    } else {
                        rc.offset = (((int) rc.thinWallTempVer[2]) & 63);
                    }
                }

                manager.getTexture(map.openedDoor).getRGB(rc.offset, 0, 1, SIZE, rc.pixels, 0, 1);

                if (rc.distance < zbuffer[raysCasted]) {
                    rc.projectedSliceHeight = (int) ((getPlayerPaneDist() << SIZE_LOG) / rc.distance);

                    if (rc.projectedSliceHeight == 0) {
                        rc.projectedSliceHeight = 1;
                    }
                    if (rc.projectedSliceHeight >= planeHeight) {
                        rc.projectedSliceHeight = planeHeight - 1;
                    }

                    if (rc.startDraw >= planeHeight) {
                        rc.startDraw = planeHeight - 1;
                    }

                    rc.startDraw = (planeHeight >> 1) - (rc.projectedSliceHeight >> 1);
                    if (rc.startDraw >= planeHeight) {
                        rc.startDraw = planeHeight - 1;
                    }

                    // Scaling function simplified to handle one pixel wide images better. Inlined for better perfomance.
                    rc.slicePixels = new int[rc.projectedSliceHeight];
                    y_ratio = (int) ((SIZE << 16) / rc.projectedSliceHeight) + 1;
                    for (int i = 0; i < rc.projectedSliceHeight; i++) {
                        if ((((i * y_ratio) >> 16) < SIZE)) {
                            rc.slicePixels[i] = rc.pixels[((i * y_ratio) >> 16)];
                        }
                    }

                    for (int wy = rc.startDraw, index = 0; wy <= rc.startDraw + rc.projectedSliceHeight - 1; wy++, index++) {
                        if (wy >= 0 && wy < planeHeight && ((rc.slicePixels[index] >> 24) & 0xff) != 0) {
                            //screenBuffer.setRGB(raysCasted, wy, rc.slicePixels[index]);
                            output[raysCasted + wy * planeWidth] = rc.slicePixels[index];
                        }
                    }
                }
            }
            // </editor-fold>
            //////////////////////////////////////////////////////////////////////////////////

            ///////////////	INCREMENTING RAY-ANGLE TO NEXT POSITION //////////////////////////
            rayAngle = (rayAngle += angleBetweenRays) >= ANGLE360 ? (rayAngle - ANGLE360) : rayAngle;
            //////////////////////////////////////////////////////////////////////////////////
        } // for loop; rays from left to righ
    }

    //<editor-fold defaultstate="collapsed" desc="sprite casting">
    /**
     * Find the screen X coordinate for a sprite on the map.
     * https://www.allegro.cc/forums/thread/355015
     *
     * @param spriteX
     * @param spriteY
     * @return x screen-coordinate of sprite
     */
    private final float findXOffsetForSprites(int spriteX, int spriteY) {
        float theta = (player.angle * 60f / planeWidth);
        float fov = getFOV() * 60f / planeWidth;

        if (theta < 0) {
            theta += 360;
        }
        if (theta >= 360) {
            theta -= 360;
        }

        int xInc = (int) ((spriteX << SIZE_LOG) - player.x);
        int yInc = (int) ((spriteY << SIZE_LOG) - player.y);

        // radian = degree * PI / 180?
        // degree = radian / PI * 180?
        float thetaTemp = (float) Math.atan2(yInc, xInc);
        thetaTemp *= 180f / Math.PI;

        if (thetaTemp < 0) {
            thetaTemp += 360;
        }

        // theta - player.angle
        
        float yTmp;
        yTmp = theta + (fov / 2) - thetaTemp;
        if (thetaTemp > 270 && theta < 90) {
           yTmp = theta + (fov / 2) - thetaTemp + 360;
        }
        if (theta > 270 && thetaTemp < 90) {
           yTmp = theta + (fov / 2) - thetaTemp - 360;
        }

        float xTmp = (float) (yTmp * planeWidth / fov);

        
        System.out.println(player.angle + "|" 
                         + theta + "|" 
                         + thetaTemp + "|" + yTmp);
        
        return planeWidth - xTmp + 32;
    }

    /**
     * Examine the existing sprites and draw slice-by-slice the ones visible in
     * the player's FOV (Field Of View).
     *
     * @param screenBuffer
     */
    private final void castSprites() {
        int[] pixels2 = new int[SIZE << SIZE_LOG];
        int[] pixels3;
        int projectedSliceHeight;
        int onscreenX;
        float distance;
        int onscreenY;
        boolean inside;
        
        // <editor-fold defaultstate="collapsed" desc="SPRITES">
        for (int i = 0; i < manager.getItemStartIndex(); i++) {

            onscreenX = (int) findXOffsetForSprites(manager.sprites[i].x, manager.sprites[i].y);

            inside = !map.isOutside(manager.sprites[i].x, manager.sprites[i].y);

            distance = manager.sprites[i].distanceFromPlayer;
            projectedSliceHeight = (int) ((getPlayerPaneDist() << (SIZE_LOG)) / distance) % planeHeight;
            
            if ((distance <= 90) ^ ((onscreenX) >= planeWidth) || projectedSliceHeight <= 0) {
                continue;
            }

            onscreenY = ((planeHeight >> 1) - (projectedSliceHeight >> 1));
            if (onscreenY < 0) {
                onscreenY = 0;
            }

            manager.sprites[i].texture.getRGB(0, 0, SIZE, SIZE, pixels2, 0, SIZE);
            pixels3 = resizePixels(pixels2, SIZE, SIZE, projectedSliceHeight, projectedSliceHeight);
            
            for (int col = 0; col < projectedSliceHeight; col++, onscreenX++) {
                if( 
                        (onscreenX < planeWidth) && ( (onscreenX) >= 0) && (col < planeWidth) && zbuffer[onscreenX] > distance
                ) {
                    for (int a = 0; a < projectedSliceHeight; a++) {
                        int index = (a * projectedSliceHeight + col) % (projectedSliceHeight*projectedSliceHeight);
                        if ( ((onscreenY + a) < planeHeight)
                          && ((pixels3[index] >> 24) != 0)
                        ) {
                            if (IS_SHADERS_ON && inside) {
                                output[ (onscreenX) + planeWidth * (onscreenY + a) ] = ImageAndPostProcessHelper.addFogEffect(pixels3[index], distance);
                            } else {
                                //output[ (onscreenX) + planeWidth * (onscreenY + a) ] = pixels3[index];
                                output[ (onscreenX) + planeWidth * (onscreenY + a) ] = ImageAndPostProcessHelper.addFogEffect(pixels3[index], distance);
                            }

                        }
                    }
                }
            } // for 2
        } // for 1
        // </editor-fold>
        
        /////////////////
        // <editor-fold defaultstate="collapsed" desc="ITEMS">
        for (int i = manager.getItemStartIndex(); i < manager.getEnemyStartIndex(); i++) {

            onscreenX = (int) findXOffsetForSprites(manager.sprites[i].x, manager.sprites[i].y);

            inside = !map.isOutside(manager.sprites[i].x, manager.sprites[i].y);

            distance = manager.sprites[i].distanceFromPlayer;
            projectedSliceHeight = (int) ((getPlayerPaneDist() << (SIZE_LOG)) / distance) % planeHeight;
            
            if ((distance <= 90) ^ ((onscreenX) >= planeWidth) || projectedSliceHeight <= 0) {
                continue;
            }

            onscreenY = ((planeHeight >> 1) - (projectedSliceHeight >> 1));
            if (onscreenY < 0) {
                onscreenY = 0;
            }
            
            manager.sprites[i].texture.getRGB(0, 0, SIZE, SIZE, pixels2, 0, SIZE);
            pixels3 = resizePixels(pixels2, SIZE, SIZE, projectedSliceHeight, projectedSliceHeight);
            
            for (int col = 0; col < projectedSliceHeight; col++, onscreenX++) {
                if( 
                        (onscreenX < planeWidth) && ( (onscreenX) >= 0) && (col < planeWidth) && zbuffer[onscreenX] > distance
                ) {
                    for (int a = 0; a < projectedSliceHeight; a++) {
                        int index = (a * projectedSliceHeight + col) % (projectedSliceHeight*projectedSliceHeight);
                        if ( ((onscreenY + a) < planeHeight)
                          && ((pixels3[index] >> 24) != 0)
                        ) {
                            if (IS_SHADERS_ON && inside) {
                                output[ (onscreenX) + planeWidth * (onscreenY + a) ] = ImageAndPostProcessHelper.addFogEffect(pixels3[index], distance);
                            } else {
                                //output[ (onscreenX) + planeWidth * (onscreenY + a) ] = pixels3[index];
                                output[ (onscreenX) + planeWidth * (onscreenY + a) ] = ImageAndPostProcessHelper.addFogEffect(pixels3[index], distance);
                            }

                        }
                    }
                }
            } // for 2
        } // for 1
        // </editor-fold>
        
        /////////////////
        
        // <editor-fold defaultstate="collapsed" desc="ENEMIES">
        for (int i = manager.getEnemyStartIndex(); i < manager.sprites.length; i++) {

            onscreenX = (int) findXOffsetForSprites(manager.sprites[i].x, manager.sprites[i].y);

            inside = !map.isOutside(manager.sprites[i].x, manager.sprites[i].y);

            distance = manager.sprites[i].distanceFromPlayer;
            projectedSliceHeight = (int) ((getPlayerPaneDist() << (SIZE_LOG)) / distance) % planeHeight;
            
            if ((distance <= 90) ^ ((onscreenX) >= planeWidth) || projectedSliceHeight <= 0) {
                continue;
            }

            onscreenY = ((planeHeight >> 1) - (projectedSliceHeight >> 1));
            if (onscreenY < 0) {
                onscreenY = 0;
            }

            manager.sprites[i].getImg().getRGB(0, 0, SIZE, SIZE, pixels2, 0, SIZE);
            pixels3 = resizePixels(pixels2, SIZE, SIZE, projectedSliceHeight, projectedSliceHeight);
            
            for (int col = 0; col < projectedSliceHeight; col++, onscreenX++) {
                if( 
                        (onscreenX < planeWidth) && ( (onscreenX) >= 0) && (col < planeWidth) && zbuffer[onscreenX] > distance
                ) {
                    for (int a = 0; a < projectedSliceHeight; a++) {
                        int index = (a * projectedSliceHeight + col) % (projectedSliceHeight*projectedSliceHeight);
                        if ( ((onscreenY + a) < planeHeight)
                          && ((pixels3[index] >> 24) != 0)
                        ) {
                            if (IS_SHADERS_ON && inside) {
                                output[ (onscreenX) + planeWidth * (onscreenY + a) ] = ImageAndPostProcessHelper.addFogEffect(pixels3[index], distance);
                            } else {
                                //output[ (onscreenX) + planeWidth * (onscreenY + a) ] = pixels3[index];
                                output[ (onscreenX) + planeWidth * (onscreenY + a) ] = ImageAndPostProcessHelper.addFogEffect(pixels3[index], distance);
                            }

                        }
                    }
                }
            } // for 2
        } // for 1
        // </editor-fold>
        
    } // casting
    //</editor-fold>

    private void drawHelp(Graphics gb) {
        String l = this.manager.getHelp();
        gb.setColor(Color.BLUE);
        int y = gb.getFontMetrics().getHeight() + 10;
        int x = gb.getFontMetrics().getHeight();
        for (String line : l.split("\n")) {
            gb.drawString(line, x, y += gb.getFontMetrics().getHeight());
        }
    }
    
    void clearScreen() {
        for(int i = 0; i < output.length; i++) {
            output[i] = 0x000000;
        }
    }

    /**
     * The function handling, calling the casting, drawing functions.
     */
    public void castGraphic() {
        // rendering walls, floor, ceiling, sprites, shaders
        
        if (IS_SKYBOX_ON) {
            //renderSky();
            renderScrollableNightSky();
        }
        
        render();
        
        if (IS_SPRITES_ON) {
            castSprites();
        }
        
        if (player.SHOOTING) {
            renderBeam();
        }
        
        if (IS_RENDER_OVERHEAD_AND_HUD) {
            renderHUDAndOverheadGraphic();
        }
        
    }
    
    private void renderSky() {
        int[] img = new int[360 * 240]; // 2560 x 240
        skybox.getImage(360, 240).getRGB(0, 0, 360, 240, img, 0, 1);
        final float xW = 360 / planeWidth;
        final float yW = 240 / (planeHeight >> 1);
        for(int y = 0; y < (planeHeight >> 1); y++) {
            for(int x = 0; x < planeWidth; x++) {
                output[y * planeWidth + x] = img[(int)(y * yW) * 360 + (int)(x * xW)];
            }
        }
    }
    
    private void renderNightSky() {
        int[] img = new int[360 * 240]; // 2560 x 240
        final float xW = 360 / planeWidth;
        final float yW = 240 / (planeHeight >> 1);
        
        Random rand = new Random(10);
        int n = rand.nextInt(500) + 1;
        
        for(int y = 0; y < (planeHeight >> 1); y++) {
            for(int x = 0; x < planeWidth; x++) {
                output[y * planeWidth + x] = n > 10 ? 0xff000000 : 0xffffffff;
                n = rand.nextInt(500) + 1;
            }
        }
    }
    
    public int skyOffset = 0;
    private void renderScrollableNightSky() {
        int[] img = new int[2160 * 240]; // 2560 x 240
        final float xW = 360 / planeWidth;
        final float yW = 240 / (planeHeight >> 1);
        Random rand = new Random(10);
        int n = rand.nextInt(500) + 1;
        int startX = player.angle;
        
        for(int y = 0; y < (planeHeight >> 1); y++) {
            for(int x = 0; x < 2160; x++) {
                img[y * 2160 + x] = n > 10 ? 0xff000000 : 0xffffffff;
                n = rand.nextInt(500) + 1;
            }
        }
        
        
        for(int y = 0; y < (planeHeight >> 1); y++) {
            for(int x = 0; x < planeWidth; x++) {
                output[y * planeWidth + x] = img[y * 2160 + ((startX+x)%2160)];
            }
        }
    }
    
    private void showPlayerData(Graphics gb) {
        gb.setColor(Color.BLUE);
        gb.fillRect(5, (planeHeight >> 1) + (planeHeight >> 2) - 15, 130, 60);
        gb.setColor(Color.YELLOW);
        gb.drawString("HP: " + player.getHp(), 10, (planeHeight >> 1) + (planeHeight >> 2));
        gb.drawString("DP: " + player.getDp(), 10, (planeHeight >> 1) + (planeHeight >> 2) + 20);
        gb.drawString("Active inventory slot: " + player.getActualItemPointer(), 10, (planeHeight >> 1) + (planeHeight >> 2) + 40);
    }
    
    public void drawMenu() {
        //g.drawImage(manager.getMenuImage(), 0, 0, this);
    }
    //////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Depending on the actual rotation of the player it rotate the skybox to
     * the opposite.
     *
     * @param LEFT
     */
    public final synchronized void synchSkyboxWithPlayer(boolean LEFT) {
        skybox.rotate(!LEFT, player.rotateSpeed);
    }

    public int getPlayerX() {
        return playerX;
    }

    public void setPlayerX(int playerX) {
        this.playerX = playerX;
    }

    public int getPlayerY() {
        return playerY;
    }

    public void setPlayerY(int playerY) {
        this.playerY = playerY;
    }

    public int getAngle() {
        return angle;
    }

    public void setAngle(int angle) {
        this.angle = angle;
    }

    public int getFOV() {
        return FOV;
    }

    public void setFOV(int fOV) {
        FOV = fOV;
    }

    public int getPlayerPaneDist() {
        return playerPaneDist;
    }

    public void setPlayerPaneDist(int playerPaneDist) {
        this.playerPaneDist = playerPaneDist;
    }

}
