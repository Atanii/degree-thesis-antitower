package hu.emanuel.jeremi.antitower.graphic;

import static hu.emanuel.jeremi.antitower.common.Tile64.*;
import static hu.emanuel.jeremi.antitower.graphic.ImageAndPostProcessHelper.*;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;

import hu.emanuel.jeremi.antitower.effect.RainEffect;
import hu.emanuel.jeremi.antitower.entity.EntityManager;
import hu.emanuel.jeremi.antitower.entity.Player;
import hu.emanuel.jeremi.antitower.message.MessageDisplayer;
import hu.emanuel.jeremi.antitower.physics.GamePhysicsHelper;
import hu.emanuel.jeremi.antitower.world.MapData;
import hu.emanuel.jeremi.antitower.world.Skybox;

public class Graphic2 extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3697273593000989014L;	
	
	/////////////////////////////// PLANE ////////////////////////////////////////////////////
	private int planeWidth = 640, planeHeight = 480;
	//////////////////////////////////////////////////////////////////////////////////////////
	
	/////////////////////////////// GRAPHICS FOR DRAWING ON SCREEN ///////////////////////////
	private Graphics gb;			// for the graphics buffer and drawing
	private BufferedImage frame;	// image for the actual frame
	//////////////////////////////////////////////////////////////////////////////////////////
	
	/////////////////////////////// ANGLES DEPENDING ON THE PLANEWIDTH ///////////////////////
	public int ANGLE0 = 0;
	public int ANGLE60 = planeWidth;
	public int ANGLE30 = ANGLE60 / 2;
	public int ANGLE360 = ANGLE60*6;
	public int ANGLE5 = ANGLE30 / 6;
	public int ANGLE90 = ANGLE30 * 3;
	public int ANGLE180 = ANGLE90*2;
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
	public boolean IS_SHADERS_ON = true;
	public boolean IS_RAIN_ON 	  = true;
	public boolean IS_SPRITES_ON = true;
	public boolean IS_HELP_ON = false;
	//////////////////////////////////////////////////////////////////////////////////////////
	
	/////////////////////////////// map LOCAL DATA ///////////////////////////////////////////
	private int mapWidth;
	private int mapHeight;
	//////////////////////////////////////////////////////////////////////////////////////////
	
	// MANAGER AND LIBRARY OBJECTS: EntityManager, MapData, TextureLibrary, MessageDisplayers
	EntityManager manager;
	MapData map;
	TextureLibrary tex;
	MessageDisplayer msgdisp;
	
	// OBJECTS FOR EFFECTS: SKYBOX, RAIN
	public Skybox skybox;
	RainEffect rain = new RainEffect(planeWidth,planeHeight);
	
	FloorThread floor;
	CeilingThread ceiling;
	
	/**
	 * Precalculate values for the tables (sinTable, cosTable, ...) and fills them.
	 */
	private void preCalculateTablesAndValues() {
		// ANGLES BASED ON THE PLANEWIDTH
		ANGLE0 = 0;
		ANGLE60 = planeWidth;
		ANGLE30 = ANGLE60 / 2;
		ANGLE360 = ANGLE60*6;
		ANGLE5 = ANGLE30 / 6;
		ANGLE90 = ANGLE30 * 3;
		ANGLE180 = ANGLE90*2;
		ANGLE270 = ANGLE90 * 3;	
		
		// DECLARING PRECALCULATED TABLES (sin,cos,...etc)
		sinTable = new float[ANGLE360+1];
		InvSinTable = new float[ANGLE360+1];
		cosTable = new float[ANGLE360+1];
		InvCosTable = new float[ANGLE360+1];
		tanTable = new float[ANGLE360+1];
		InvTanTable = new float[ANGLE360+1];
		xStepTable = new float[ANGLE360+1];
		yStepTable = new float[ANGLE360+1];
		// fisheye correction table
		fishEyeCorrectionTable = new float[ANGLE60+1];
		
		// FILLING PRECALCULATED TABLES
		for(int i = 0; i <= ANGLE360; i++) {
			sinTable[i] = (float) Math.sin(GamePhysicsHelper.toCustomRad((float)i,ANGLE180));
			InvSinTable[i] = 1.0f / sinTable[i];
			cosTable[i] = (float) Math.cos(GamePhysicsHelper.toCustomRad((float)i,ANGLE180));
			InvCosTable[i] = 1.0f / cosTable[i];
			tanTable[i] = (float) Math.tan(GamePhysicsHelper.toCustomRad((float)i,ANGLE180));
			InvTanTable[i] = 1.0f / tanTable[i];
			// Facing left:
			if(i >= ANGLE90 && i < ANGLE270) {
				xStepTable[i] = SIZE / tanTable[i];				
				xStepTable[i] = xStepTable[i] > 0 ? -xStepTable[i] : xStepTable[i];
			} else {
				xStepTable[i] = SIZE / tanTable[i];
				xStepTable[i] = xStepTable[i] < 0 ? -xStepTable[i] : xStepTable[i];
			}
			
			// Facing down:
			if(i >= ANGLE0 && i < ANGLE180) {
				yStepTable[i] = SIZE * tanTable[i];
				yStepTable[i] = yStepTable[i] < 0 ? -yStepTable[i] : yStepTable[i];
			} else {
				yStepTable[i] = SIZE * tanTable[i];
				yStepTable[i] = yStepTable[i] > 0 ? -yStepTable[i] : yStepTable[i];
			}
		}
		// FILLING FISHEYE CORRECTION TABLE
		for(int i = -ANGLE30 ; i <= ANGLE30; i++) {
			fishEyeCorrectionTable[i+ANGLE30] = (float) (1.0f / Math.cos(GamePhysicsHelper.toCustomRad((float)i,ANGLE180)));
		}
		
	}
	
	/**
	 * Since 
	 */
	private void setupPlayer() {
		// TODO: get rid of this
		player.FOV = ANGLE60; setFOV(player.FOV);
		player.speed = 10;
		player.rotateSpeed = ANGLE5;
		player.playerPaneDist = (int) ((planeWidth >> 1) / (float)Math.tan(GamePhysicsHelper.toCustomRad(player.FOV>>1,ANGLE180)));
		player.x = 2*SIZE;
		player.y = 2*SIZE;
		player.isInside = false;
		player.angle = ANGLE0;
		angleBetweenRays = getFOV()/planeWidth;
	}
	
	/**
	 * 
	 */
	private void initBuffersForSpriteCasting() {
		zbuffer = new float[planeWidth];
	}
		
	// Program constructor:
	
	/**
	 * Setup: jframe and gameField
	 * It calls the function for filling the sinTable, cosTable, ...etc.
	 * Hide cursor by make it equal with the transparentCursor.
	 * It starts the gameThread.
	 */
	public Graphic2(int planeWidth, int planeHeight,Player player, EntityManager manager) {
		super();
		
		this.manager = manager;
		this.tex = manager.texs;
		this.map = manager.map;
		
		mapWidth = map.width;
		mapHeight = map.height;
		
		frame = new BufferedImage(planeWidth,planeHeight,BufferedImage.TYPE_INT_ARGB);
		this.setIgnoreRepaint(true);
		
		this.planeWidth = planeWidth;
		this.planeHeight = planeHeight;
		this.player = player;
		
		preCalculateTablesAndValues();
		setupPlayer();
		initBuffersForSpriteCasting();
		
		msgdisp = new MessageDisplayer(manager.msgh);
		
		skybox = new Skybox(player.FOV,tex.loadAndGetTextureFromImageFile("skybox_2560x240.png", 656345));
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawImage(frame, 0, 0, this);
		renderHUDAndOverheadGraphic(g);
	}
	
	/////////////////////////////// RAY-CASTING //////////////////////////////////////////////
	/**
	 * Renders the overhead graphic ie. HUD, perspective image of the actual item...etc.
	 * 
	 * @param g
	 */
	public final void renderHUDAndOverheadGraphic(Graphics g) {
		g.drawImage(tex.getItem(player.getSelectedItem().overheadImg), (planeWidth>>1)-(SIZE), planeHeight-(SIZE<<1),SIZE<<1,SIZE<<1, this);
	}
	
	/**
	 * Draws the beam of the weapon handled by the player. It's always positioned to the center of the screen.
	 * 
	 */
	public final void renderBeam() {
		gb.setColor(Color.GREEN);
		gb.drawLine(planeWidth>>1, planeHeight-1, planeWidth>>1, planeHeight>>1);
	}
	
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
		
		// STOREY WALL
		int storeyPixels[] = new int[SIZE];
		int storeySlicePixels[];
		int storeyIdVer = -1;
		int storeyIdHor = -1;
		boolean isThereStoreyVer;
		boolean isThereStoreyHor;		
		// VIRTUAL WALL
		boolean isThereVirtualWallAndStorey;
		boolean isVirtualVer;
		boolean isVirtualHor;
		int v_offset;														// texture offset
		float v_wallDistHorizontal, v_wallDistVertical, v_distance = .0f;	// Horizontal and vertical wall distances:
		float v_tempAx = .0f, v_tempAy = .0f;								// wall-slice coordinate for computing texture offset
		int v_projectedSliceHeight;											// this height what you'll see
		float v_horizontalHeight;
		float v_verticalHeight;
		
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
			tempAx = .0f; tempAy = .0f;							// wall-slice coordinate for computing texture offset
			
			// arrays for the texture pixels and the slice pixels
			// all of the textures has the size of SIZE
			pixels = new int[SIZE];			
			storeyPixels = new int[SIZE];
			storeyIdVer = -1;
			storeyIdHor = -1;
			
			// spritecasting		
			tileIndexHor = 0; tileIndexVer = 0;
			
			// thin walls
			thinWallTempHor = new float[3];
			thinWallTempVer = new float[3];
			isThereThinWallHor = isThereThinWallVer = false;
			isHorizontalWallInside = isVerticalWallInside = false;
			
			// virtual
			v_distance = .0f;										// Horizontal and vertical wall distances:
			v_tempAx = .0f; v_tempAy = .0f;							// wall-slice coordinate for computing texture offset
		}
		
		public void reset() {
			// no thin-wall is the default
			isThereThinWallHor = false;
			isThereThinWallVer = false;
			thinWallTempHor[0] = Float.MAX_VALUE;
			thinWallTempVer[0] = Float.MAX_VALUE;
			
			storeyIdVer = -1;
			storeyIdHor = -1;
			
			isClosedDoor = false;
			
			// default wall height
			verticalHeight = horizontalHeight = SIZE;
			
			// default state is inside
			isHorizontalWallInside = true;
			isVerticalWallInside = true;
			
			// default is no storey
			isThereStoreyVer = false;
			isThereStoreyHor = false;
			isThereVirtualWallAndStorey = false;
			isVirtualVer = false;
			isVirtualHor = false;
			v_verticalHeight = v_horizontalHeight = SIZE;
			v_wallDistHorizontal = v_wallDistVertical = Float.MAX_VALUE;
			
			v_startDraw = startDraw = Integer.MAX_VALUE;	
		}
	} private RayCastedGridData rc = new RayCastedGridData();
	
	/**
	 * The most important function of the ray-casting engine. It casts rays from the position of the player towards
	 * the map-cells in the range of the screen (FOV - Field Of View, possibly 60°) until the ray hits a wall.
	 * If the ray hit a wall, then depending on the player - wall coordinate distance this function determine the height,
	 * texture offset, screen position of the specific wall slice and draws it into the screenbuffer.
	 * 
	 * The function starts at the leftmost coloumn of the screen (0°) and ends at the rightmost (60°).
	 * 
	 * When all rays are casted, the screenbuffer will be drawn onto screen.
	 */
	public final void castOutsideOOP() {
		
		rc.hardReset();
		
		// The angle of the angle to be casted:
		int rayAngle = (rayAngle = getAngle() - ANGLE30) < 0 ? rayAngle + ANGLE360 : rayAngle;
		
		// buffer for frames
		BufferedImage screenBuffer = new BufferedImage(planeWidth,planeHeight,BufferedImage.TYPE_INT_ARGB);
		gb = screenBuffer.createGraphics();
		
		// skybox and background
		gb.setColor(Color.GRAY);
		gb.fillRect(0, planeHeight>>1, planeWidth, planeHeight>>1);
		gb.drawImage(skybox.getImage(planeWidth, planeHeight>>1), 0, 0, this);
		
		// Loop from left to right
		for( int raysCasted = 0; raysCasted < planeWidth; raysCasted += angleBetweenRays) {
			
			// START VALUES FOR VARIABLES, DISTANCES...ETC
			
			// default distance
			zbuffer[raysCasted] = Float.MAX_VALUE;
			
			rc.reset();
			
			///////////////	HORIZONTAL INTERSECTIONS /////////////////////////////////////////
			if( (rayAngle > ANGLE0) && (rayAngle < ANGLE180) ) {	// facing down
				
				// angle > ANGLE 0 && angle < ANGLE 180
				//System.out.println("You're facing down!");
				rc.DOWN = true;
				rc.Ya = SIZE;
				rc.Ay =  ( (( ( (getPlayerY()>>SIZE_LOG) ) )) << SIZE_LOG ) + SIZE ;
				rc.Ax = getPlayerX() + (InvTanTable[rayAngle] * (rc.Ay-getPlayerY()));
			} else {
				// ! (angle > ANGLE 0 && angle < ANGLE 180)
				//System.out.println("You're facing up!");
				rc.DOWN = false;
				rc.Ya = -SIZE;
				rc.Ay = (( (getPlayerY()>>SIZE_LOG) )) << SIZE_LOG;
				rc.Ax = getPlayerX() + (InvTanTable[rayAngle] * (--rc.Ay-getPlayerY()));
				//Ay -= 1.0;
			}
			rc.Xa = xStepTable[rayAngle];			
			while(true) {
				// GRID COORDINATES (ARRAY COORDINATE)
				rc.gridX = (int) Math.floor(rc.Ax / SIZE);
				rc.gridY = (int) Math.floor(rc.Ay / SIZE);
				// CHECK ANGLE
				if( (rayAngle == ANGLE0) ^ (rayAngle == ANGLE180) ) {
					rc.wallDistHorizontal = Float.MAX_VALUE;
					break;
				}			
				// OUT OF GRID ?
				if( ((rc.gridX < 0) ^ (rc.gridX >= mapWidth)) || ((rc.gridY < 0) ^ (rc.gridY >= mapHeight)) ) {
					rc.wallDistHorizontal = Float.MAX_VALUE;
					break;
				}
				// THIN WALL
				if( !rc.isThereThinWallHor && map.isDoor(rc.gridX, rc.gridY) ) {
					if( map.isOpen(rc.gridX, rc.gridY) ) {
						rc.thinWallTempHor[0] = Math.abs( (rc.Ax-getPlayerX())*InvCosTable[rayAngle] );
						rc.thinWallTempHor[1] = DOOR_OPENED;
						rc.thinWallTempHor[2] = (float) Math.floor(rc.Ax);
						rc.isThereThinWallHor = true;
					} else {
						rc.isClosedDoor = true;
						rc.wallDistHorizontal = Math.abs( (rc.Ax-getPlayerX())*InvCosTable[rayAngle] );
						rc.tileIndexHor = DOOR_CLOSED;
						rc.tempAx = (float) Math.floor(rc.Ax);
						zbuffer[raysCasted] = (float) Math.sqrt( (getPlayerX()-( (rc.gridX<<SIZE_LOG) + (SIZE>>1) ))*(getPlayerX()-( (rc.gridX<<SIZE_LOG) + (SIZE>>1) ))+
								 								 (getPlayerY()-( (rc.gridY<<SIZE_LOG) + (SIZE>>1) ))*(getPlayerY()-( (rc.gridY<<SIZE_LOG) + (SIZE>>1) )) );
						break;
					}
				}
				// VIRTUAL WALL
				if( map.isVirtual(rc.gridX, rc.gridY) ) {
					rc.isVirtualHor = true;
					
					if(!map.isOutside(rc.gridX, rc.gridY)) {
						// STOREY
						if(!player.isInside) {
							rc.storeyIdHor = map.getStorey(rc.gridX, rc.gridY);
							if( rc.storeyIdHor != -1 ) {
								rc.isThereStoreyHor = true;
							}
						}
					}
					
					rc.v_wallDistHorizontal = Math.abs( (rc.Ax-getPlayerX())*InvCosTable[rayAngle] );
					rc.v_tempAx = (float) Math.floor(rc.Ax);
					
					rc.v_horizontalHeight = map.getHeight(rc.gridX, rc.gridY);
				}
				// NORMAL WALL
				if( map.isWall(rc.gridX, rc.gridY) ) {
					
					if(map.isOutside(rc.gridX, rc.gridY)) {
						// STOREY
						if(!player.isInside) {
							rc.storeyIdHor = map.getStorey(rc.gridX, rc.gridY);
							if( rc.storeyIdHor != -1 ) {
								rc.isThereStoreyHor = true;
							}
						}
						
						rc.isHorizontalWallInside = false;
					}						
					
					rc.wallDistHorizontal = Math.abs( (rc.Ax-getPlayerX())*InvCosTable[rayAngle] );
					rc.tileIndexHor = map.texMap[rc.gridY*mapWidth+rc.gridX];
					rc.tempAx = (float) Math.floor(rc.Ax);
					zbuffer[raysCasted] = (float) Math.sqrt( (getPlayerX()-( (rc.gridX<<SIZE_LOG) + (SIZE>>1) ))*(getPlayerX()-( (rc.gridX<<SIZE_LOG) + (SIZE>>1) ))+
							 								 (getPlayerY()-( (rc.gridY<<SIZE_LOG) + (SIZE>>1) ))*(getPlayerY()-( (rc.gridY<<SIZE_LOG) + (SIZE>>1) )) );
					
					rc.horizontalHeight = map.getHeight(rc.gridX, rc.gridY);
					
					break;
				}		
				// NEXT STEP
				rc.Ax += rc.Xa;
				rc.Ay += rc.Ya;
			} // while(true)
		    //////////////////////////////////////////////////////////////////////////////////
			
			///////////////	VERTICAL INTERSECTIONS ///////////////////////////////////////////
			if( (rayAngle < ANGLE90) ^ (rayAngle > ANGLE270) ) {	// facing right
				rc.RIGHT = true;
				rc.Xa = SIZE;
				rc.Ax = ((getPlayerX()>>SIZE_LOG) << SIZE_LOG) + SIZE;
				rc.Ay = getPlayerY() + (tanTable[rayAngle] * (rc.Ax - getPlayerX()));
			} else {												// facing left
				rc.RIGHT = false;
				rc.Xa = -SIZE;
				rc.Ax = ((getPlayerX()>>SIZE_LOG) << SIZE_LOG);
				rc.Ay = getPlayerY() + (tanTable[rayAngle] * (--rc.Ax - getPlayerX()));
				//Ax -= 1.0;
			}
			rc.Ya = yStepTable[rayAngle];
			while(true) {
				// GRID COORDINATES (ARRAY COORDINATE)
				rc.gridX = (int) Math.floor(rc.Ax / SIZE);
				rc.gridY = (int) Math.floor(rc.Ay / SIZE);
				// CHECK ANGLE
				if( (rayAngle == ANGLE90) ^ (rayAngle == ANGLE270) ) {
					rc.wallDistVertical = Float.MAX_VALUE;
					break;
				}
				// OUT OF GRID ?
				if( ((rc.gridX < 0) ^ (rc.gridX >= mapWidth)) || ((rc.gridY < 0) ^ (rc.gridY >= mapHeight)) ) {
					rc.wallDistVertical = Float.MAX_VALUE;
					break;
				}
				// THIN WALL
				if( !rc.isThereThinWallVer && map.isDoor(rc.gridX, rc.gridY) ) {
					if( map.isOpen(rc.gridX, rc.gridY) ) {
						rc.thinWallTempVer[0] = Math.abs( (rc.Ay-getPlayerY())*InvSinTable[rayAngle] );
						rc.thinWallTempVer[1] = DOOR_OPENED;
						rc.thinWallTempVer[2] = rc.Ay;
						rc.isThereThinWallVer = true;
					} else {
						rc.isClosedDoor = true;
						rc.wallDistVertical = Math.abs( (rc.Ay-getPlayerY())*InvSinTable[rayAngle] );
						rc.tileIndexVer = DOOR_CLOSED;
						rc.tempAy = rc.Ay;
						zbuffer[raysCasted] = (float) Math.sqrt( (getPlayerX()-( (rc.gridX<<SIZE_LOG) + (SIZE>>1) ))*(getPlayerX()-( (rc.gridX<<SIZE_LOG) + (SIZE>>1) ))+
								 								 (getPlayerY()-( (rc.gridY<<SIZE_LOG) + (SIZE>>1) ))*(getPlayerY()-( (rc.gridY<<SIZE_LOG) + (SIZE>>1) )) );
						break;
					}
				}
				// VIRTUAL WALL
				if( map.isVirtual(rc.gridX, rc.gridY) ) {
					rc.isVirtualVer = true;
					
					if(!map.isOutside(rc.gridX, rc.gridY)) {
						// STOREY
						if(!player.isInside) {
							rc.storeyIdVer = map.getStorey(rc.gridX, rc.gridY);
							if( rc.storeyIdVer != -1 ) {
								rc.isThereStoreyVer = true;
							}
						}
					}						
					
					rc.v_wallDistVertical = Math.abs( (rc.Ay-getPlayerY())*InvSinTable[rayAngle]);
					rc.v_tempAy = rc.Ay;
					
					rc.v_verticalHeight = map.getHeight(rc.gridX, rc.gridY);
				}
				// NORMAL WALL
				if( map.isWall(rc.gridX, rc.gridY) ) {
					
					if(map.isOutside(rc.gridX, rc.gridY)) {
						// STOREY
						if(!player.isInside) {
							rc.storeyIdVer = map.getStorey(rc.gridX, rc.gridY);
							if( rc.storeyIdVer != -1 ) {
								rc.isThereStoreyVer = true;
							}
						}
						
						rc.isVerticalWallInside = false;
					}						
					
					rc.wallDistVertical = Math.abs( (rc.Ay-getPlayerY())*InvSinTable[rayAngle]);
					rc.tileIndexVer = map.texMap[rc.gridY*mapWidth+rc.gridX];	
					rc.tempAy = rc.Ay;
					
					rc.verticalHeight = map.getHeight(rc.gridX, rc.gridY);
					
					break;
				}	
				// NEXT STEP
				rc.Ay += rc.Ya;
				rc.Ax += rc.Xa;
			} // while(true)			
		    //////////////////////////////////////////////////////////////////////////////////
			
			///////////////	TEXTURE PIXEL OFFSET /////////////////////////////////////////////
			// HORIZONTAL
			if(rc.wallDistHorizontal < rc.wallDistVertical) {
				rc.distance = rc.wallDistHorizontal/fishEyeCorrectionTable[raysCasted];
				zbuffer[raysCasted] = rc.distance;					
				if(rc.DOWN) {
					rc.offset = 63 - ((((int)(rc.tempAx))) & 63);
					
					manager.getTexture(rc.tileIndexHor).getRGB(rc.offset, 0, 1, SIZE, rc.pixels, 0, 1);
				} else {
					rc.offset = ((((int)(rc.tempAx))) & 63);
					manager.getTexture(rc.tileIndexHor).getRGB(rc.offset, 0, 1, SIZE, rc.pixels, 0, 1);
				}
				if(rc.storeyIdHor >= 0) {
					manager.getTexture(rc.storeyIdHor).getRGB(rc.offset, 0, 1, SIZE, rc.storeyPixels, 0, 1);
				}
			// VERTICAL
			} else {
				rc.isHorizontalWallInside = rc.isVerticalWallInside;
				rc.horizontalHeight = rc.verticalHeight;
				rc.distance = rc.wallDistVertical/fishEyeCorrectionTable[raysCasted];
				zbuffer[raysCasted] = rc.distance;					
				if(!rc.RIGHT) {
					rc.offset = 63 - ((((int)(rc.tempAy))) & 63);
					manager.getTexture(rc.tileIndexVer).getRGB(rc.offset, 0, 1, SIZE, rc.pixels, 0, 1);
				} else {
					rc.offset = ((((int)(rc.tempAy))) & 63);
					manager.getTexture(rc.tileIndexVer).getRGB(rc.offset, 0, 1, SIZE, rc.pixels, 0, 1);
				}
				if(rc.storeyIdVer >= 0) {
					manager.getTexture(rc.storeyIdVer).getRGB(rc.offset, 0, 1, SIZE, rc.storeyPixels, 0, 1);
				}
			}	
			///////////////	VIRTUAL TEXTURE PIXEL OFFSET & CALCULATIONS //////////////////////			
			// HORIZONTAL
			if( rc.v_wallDistHorizontal != Float.MAX_VALUE || rc.v_wallDistVertical != Float.MAX_VALUE ) {
				rc.isThereVirtualWallAndStorey = true;
				
				if( rc.v_wallDistHorizontal < rc.v_wallDistVertical ) {
					rc.v_distance = rc.v_wallDistHorizontal/fishEyeCorrectionTable[raysCasted];
					if(rc.DOWN) {
						rc.v_offset = 63 - ((((int)(rc.v_tempAx))) & 63);
					} else {
						rc.v_offset = ((((int)(rc.v_tempAx))) & 63);
					}
					if(rc.storeyIdHor >= 0) {
						manager.getTexture(rc.storeyIdHor).getRGB(rc.v_offset, 0, 1, SIZE, rc.storeyPixels, 0, 1);
					}
				// VERTICAL
				} else {
					rc.isHorizontalWallInside = rc.isVerticalWallInside;
					rc.v_horizontalHeight = rc.v_verticalHeight;
					rc.v_distance = rc.v_wallDistVertical/fishEyeCorrectionTable[raysCasted];
					if(!rc.RIGHT) {
						rc.v_offset = 63 - ((((int)(rc.v_tempAy))) & 63);
					} else {
						rc.v_offset = ((((int)(rc.v_tempAy))) & 63);
					}
					if(rc.storeyIdVer >= 0) {
						manager.getTexture(rc.storeyIdVer).getRGB(rc.v_offset, 0, 1, SIZE, rc.storeyPixels, 0, 1);
					}
				}				
				///////////////	WALL DISTANCE AND STARTDRAW VALUE ////////////////////////////
				// sliceheight relative to SIZE (64 is the common) is mandatory to determine if the wall sunken or risen
				int v_64_sliceHeight =  (int) ((getPlayerPaneDist() << SIZE_LOG) / rc.v_distance);
				// real sliceheight relative to the actual size
				rc.v_projectedSliceHeight = (int) ((getPlayerPaneDist() * rc.v_horizontalHeight) / rc.v_distance);
				
				// "smallest" wall size must be zero
				if(rc.v_projectedSliceHeight<0)
					rc.v_projectedSliceHeight = 0;
				
				// smaller than normal walls are sunken while higher than normal walls are risen (relative to SIZE)
				int v_sunkenOrRisen = rc.v_projectedSliceHeight - v_64_sliceHeight;
				
				// startDraw is the point on the screen where the wall-drawing starts
				rc.v_startDraw = ((planeHeight-v_sunkenOrRisen)>>1)-(rc.v_projectedSliceHeight>>1);
				if(rc.v_startDraw>=planeHeight) rc.v_startDraw = planeHeight-1;			
			    //////////////////////////////////////////////////////////////////////////////	
			}	
		    //////////////////////////////////////////////////////////////////////////////////
			
			// TODO: ezt eltüntetni
			float shaderDistance = rc.distance;
			
			///////////////	WALL DISTANCE AND STARTDRAW VALUE ////////////////////////////////
			// sliceheight relative to SIZE (64 is the common) is mandatory to determine if the wall sunken or risen
			int _64_sliceHeight =  (int) ((getPlayerPaneDist() << SIZE_LOG) / rc.distance);
			// real sliceheight relative to the actual size
			rc.projectedSliceHeight = (int) ((getPlayerPaneDist() * rc.horizontalHeight) / rc.distance);
			
			// "smallest" wall size must be zero
			if(rc.projectedSliceHeight<0)
				rc.projectedSliceHeight = 0;
			
			// smaller than normal walls are sunken while higher than normal walls are risen (relative to SIZE)
			int sunkenOrRisen = rc.projectedSliceHeight - _64_sliceHeight;
			
			// startDraw is the point on the screen where the wall-drawing starts
			rc.startDraw = ((planeHeight-sunkenOrRisen)>>1)-(rc.projectedSliceHeight>>1);
			if(rc.startDraw>=planeHeight) rc.startDraw = planeHeight-1;			
		    //////////////////////////////////////////////////////////////////////////////////
		    
		    /////////////// FLOOR-CASTING ////////////////////////////////////////////////////			
			// 90%-kát magam csináltam meg a matek alapján, maradék 10%-ék (indexelés és angle+rayAngle) innen van: 
			// https://www.allegro.cc/forums/thread/374305
			// Nem mûködtek a megoldások, ezért egyszerûsítettem rajtuk.
			// Stack postom: https://gamedev.stackexchange.com/questions/159285/ray-casting-floor-casting-part-fails			 
			int floorCastingStartPixel = rc.startDraw+rc.projectedSliceHeight;
			int x,y;
			if(floorCastingStartPixel < planeHeight) {
				for (int i=floorCastingStartPixel; i<=planeHeight-1; i++) {
					// floor distance
					rc.distance = ((float) (((float)PLAYERHEIGHT / (i-(planeHeight>>1)) )* getPlayerPaneDist() ) ) * fishEyeCorrectionTable[raysCasted];
					
					// floor-tile coordinate in the world
					x = ( (int) (rc.distance * (cosTable[rayAngle])) ) + getPlayerX();
					y = ( (int) (rc.distance * (sinTable[rayAngle])) ) + getPlayerY();
					
					// floor-tile coordinate on the grid
					int mapX = x>>6;
					int mapY = y>>6;
					
					// check if out of grid
					if(mapX < 0 || mapY < 0 || mapX >= mapWidth || mapY >= mapHeight)
						continue;
					
					// outside or inside
					// x & 63 = x % 64 and x & 63 = x % 64, texture offset
					// TODO: x&63 helyett x&(SIZE-1)-el ekvivalens általánosítás
					if( !map.isOutside(mapX, mapY) ) {
						if(IS_SHADERS_ON) {
							screenBuffer.setRGB(raysCasted, i, addFogEffect(manager.getTexture(map.texMap[mapY*mapWidth+mapX]).getRGB(x&63,y&63),rc.distance));
						} else {
							screenBuffer.setRGB(raysCasted, i, manager.getTexture(map.texMap[mapY*mapWidth+mapX]).getRGB(x&63,y&63));
						}
					} else {
						screenBuffer.setRGB(raysCasted, i, manager.getTexture(map.texMap[mapY*mapWidth+mapX]).getRGB(x&63,y&63));
					}
					
				}
			}
			//////////////////////////////////////////////////////////////////////////////////
			
			/////////////// CEIL-CASTING ////////////////////////////////////////////////////
			if(rc.startDraw>=0) {
				for (int i=0; i<=rc.startDraw; i++) {
					// ceil distance
					rc.distance = (float) (Math.abs(((float)PLAYERHEIGHT / (i-(planeHeight>>1)) )* getPlayerPaneDist() )) * fishEyeCorrectionTable[raysCasted];
					
					// ceil world coordinates
					x = ( (int) (rc.distance * (cosTable[rayAngle])) ) + getPlayerX();
					y = ( (int) (rc.distance * (sinTable[rayAngle])) ) + getPlayerY();
					
					// ceil grid coordinates
					int mapX = x>>6;
					int mapY = y>>6;
					
					// check if outside of grid
					if(mapX < 0 || mapY < 0 || mapX >= mapWidth || mapY >= mapHeight)
						continue;
					
					// outside or inside
					// x & 63 = x % 64 and x & 63 = x % 64, texture offset
					// TODO: x&63 helyett x&(SIZE-1)-el ekvivalens általánosítás
					// TODO: megjavítani
					try {
						if( !map.isOutside(mapX, mapY) ) {
							if(IS_SHADERS_ON) {
								screenBuffer.setRGB(raysCasted, i, addFogEffect(manager.getTexture(map.ceiling).getRGB(x&63,y&63),rc.distance));
							} else {
								screenBuffer.setRGB(raysCasted, i, manager.getTexture(map.ceiling).getRGB(x&63,y&63));
							}						
						}						
					} catch (Exception e) {
						System.out.println("x:"+(x>>6)+"|y:"+(y>>6));
						e.printStackTrace();
					};
				}
			}
			//////////////////////////////////////////////////////////////////////////////////
			
			///////////////	SCALING TEXTURE SLICE ////////////////////////////////////////////
			int y_ratio;
			if(rc.isThereVirtualWallAndStorey) {
				// Scaling function simplified to handle one pixel wide images better. Inlined for better perfomance.
				if(rc.projectedSliceHeight==0) rc.projectedSliceHeight = 1;
				rc.slicePixels = new int[rc.projectedSliceHeight];
				rc.storeySlicePixels = new int[rc.v_projectedSliceHeight];
			    y_ratio = (int)((SIZE<<16)/rc.projectedSliceHeight)+1;
			    for (int i=0;i<rc.projectedSliceHeight;i++) {
			    	if(((i*y_ratio)>>16) < SIZE ) {
			    		// wall
			    		rc.slicePixels[i] = rc.pixels[((i*y_ratio)>>16)] ;
			    	}	
			    }
			    y_ratio = (int)((SIZE<<16)/rc.v_projectedSliceHeight)+1;
			    for (int i=0;i<rc.v_projectedSliceHeight;i++) {
			    	if(((i*y_ratio)>>16) < SIZE ) {
			    		// storey
			    		if(rc.isThereStoreyVer || rc.isThereStoreyHor) {
			    			rc.storeySlicePixels[i] = rc.storeyPixels[((i*y_ratio)>>16)];
			    		}
			    	}	
			    }
			} else {
				// Scaling function simplified to handle one pixel wide images better. Inlined for better perfomance.
				if(rc.projectedSliceHeight==0) rc.projectedSliceHeight = 1;
				rc.slicePixels = new int[rc.projectedSliceHeight];
				rc.storeySlicePixels = new int[rc.projectedSliceHeight];
			    y_ratio = (int)((SIZE<<16)/rc.projectedSliceHeight)+1;
			    for (int i=0;i<rc.projectedSliceHeight;i++) {
			    	if(((i*y_ratio)>>16) < SIZE ) {
			    		// wall
			    		rc.slicePixels[i] = rc.pixels[((i*y_ratio)>>16)] ;
			    		// storey
			    		if(rc.isThereStoreyVer || rc.isThereStoreyHor) {
			    			rc.storeySlicePixels[i] = rc.storeyPixels[((i*y_ratio)>>16)];
			    		}
			    	}	
			    }		
			}
		    //////////////////////////////////////////////////////////////////////////////////
		    
		    ///////////////	DRAWING WALL SLICE ///////////////////////////////////////////////
		    if(!rc.isClosedDoor && rc.isHorizontalWallInside && IS_SHADERS_ON) {
	    		rc.slicePixels = addFogEffect(rc.slicePixels,shaderDistance);
		    }
		    
		    if(rc.isThereVirtualWallAndStorey) {
		    	
		    	for(int wy2 = rc.v_startDraw-rc.v_projectedSliceHeight, index = 0; 
			    		wy2 <= rc.v_startDraw-1; wy2++, index++) {
			    	if( (rc.isThereStoreyVer || rc.isThereStoreyHor) && wy2 >= 0 && wy2 < planeHeight && index < rc.storeySlicePixels.length) {
			    		screenBuffer.setRGB(raysCasted, wy2, rc.storeySlicePixels[index]);
			    	}
			    }
		    	
		    	for(int wy = rc.startDraw, index = 0; 
			    		wy <= rc.startDraw+rc.projectedSliceHeight-1; wy++, index++) {
			    	if(wy >= 0 && wy < planeHeight) {
			    		screenBuffer.setRGB(raysCasted, wy, rc.slicePixels[index]);
			    	}
			    }
		    	
		    } else {
		    	for(int wy = rc.startDraw, wy2 = rc.startDraw-rc.projectedSliceHeight, index = 0; 
			    		wy <= rc.startDraw+rc.projectedSliceHeight-1 || wy2 <= rc.startDraw-1; wy++, wy2++, index++) {
			    	if( (rc.isThereStoreyVer || rc.isThereStoreyHor) && wy2 >= 0 && wy2 < planeHeight) {
			    		screenBuffer.setRGB(raysCasted, wy2, rc.storeySlicePixels[index]);
			    	}
			    	if(wy >= 0 && wy < planeHeight) {
			    		screenBuffer.setRGB(raysCasted, wy, rc.slicePixels[index]);
			    	}
			    }
		    }
			//////////////////////////////////////////////////////////////////////////////////
		    
			///////////////	SCALING AND DRAWING THIN WALL ////////////////////////////////////
			/*
			thinWallTemp...[0] =  DISTANCE FROM PLAYER // Math.abs( (Ax-playerX)*InvCosTable[rayAngle] );
			thinWallTemp...[1] =  TILE TEXTURE INDEX   // DOOR_OPENED;
			thinWallTemp...[2] =  AX or AY             // (float) Math.floor(Ax);	// tempAx
			*/
			if(rc.isThereThinWallVer || rc.isThereThinWallHor) {
				
				if(rc.thinWallTempHor[0] < rc.thinWallTempVer[0]) {
					rc.distance = (int) rc.thinWallTempHor[0]/fishEyeCorrectionTable[raysCasted];
					
					if(rc.DOWN) {
						rc.offset = 63 - ((((int)(rc.thinWallTempHor[2]))) & 63);
					} else {
						rc.offset = ((((int)(rc.thinWallTempHor[2]))) & 63);
					}			
					
				} else {
					rc.distance = (int) rc.thinWallTempVer[0]/fishEyeCorrectionTable[raysCasted];
					
					if(!rc.RIGHT) {
						rc.offset = 63 - ((((int)(rc.thinWallTempVer[2]))) & 63);
					} else {
						rc.offset = ((((int)(rc.thinWallTempVer[2]))) & 63);
					}	
				}
				
				manager.getTexture(map.openedDoor).getRGB(rc.offset, 0, 1, SIZE, rc.pixels, 0, 1);
				
				if(rc.distance < zbuffer[raysCasted]) {					
					rc.projectedSliceHeight = (int) ((getPlayerPaneDist()<<SIZE_LOG) / rc.distance);
					
					if(rc.projectedSliceHeight == 0) rc.projectedSliceHeight = 1;
					if(rc.projectedSliceHeight >= planeHeight) rc.projectedSliceHeight = planeHeight-1;
					
					if(rc.startDraw>=planeHeight) rc.startDraw = planeHeight-1;
					
					rc.startDraw = (planeHeight>>1)-(rc.projectedSliceHeight>>1);
					if(rc.startDraw>=planeHeight) rc.startDraw = planeHeight-1;
					
					// Scaling function simplified to handle one pixel wide images better. Inlined for better perfomance.
					rc.slicePixels = new int[rc.projectedSliceHeight];    
				    y_ratio = (int)((SIZE<<16)/rc.projectedSliceHeight)+1;
				    for (int i=0;i<rc.projectedSliceHeight;i++) {
				    	if( (((i*y_ratio)>>16) < SIZE) )
				    			rc.slicePixels[i] = rc.pixels[((i*y_ratio)>>16)] ;
				    }
				    
				    for(int wy = rc.startDraw, index = 0; wy <= rc.startDraw+rc.projectedSliceHeight-1; wy++, index++) {
				    	if(wy >= 0 && wy < planeHeight && ( (rc.slicePixels[index]>>24) & 0xff ) != 0 ) {
				    		screenBuffer.setRGB(raysCasted, wy, rc.slicePixels[index]);
				    	}
				    }
				}
				
				
			}
			//////////////////////////////////////////////////////////////////////////////////
						
			///////////////	INCREMENTING RAY-ANGLE TO NEXT POSITION //////////////////////////
			rayAngle = (rayAngle+=angleBetweenRays) >= ANGLE360 ? (rayAngle - ANGLE360) : rayAngle;
			//////////////////////////////////////////////////////////////////////////////////
			
		} // for loop; rays from left to right
		
		if(IS_SPRITES_ON)
			castSprites(screenBuffer);
		
		// Preparing the actual frame to be drawn.
		frame = screenBuffer;
	}
		
	/**
	 * The most important function of the ray-casting engine. It casts rays from the position of the player towards
	 * the map-cells in the range of the screen (FOV - Field Of View, possibly 60°) until the ray hits a wall.
	 * If the ray hit a wall, then depending on the player - wall coordinate distance this function determine the height,
	 * texture offset, screen position of the specific wall slice and draws it into the screenbuffer.
	 * 
	 * The function starts at the leftmost coloumn of the screen (0°) and ends at the rightmost (60°).
	 * 
	 * When all rays are casted, the screenbuffer will be drawn onto screen.
	 */
	public final void castOutside() {
		
		// The angle of the angle to be casted:
		int rayAngle = (rayAngle = getAngle() - ANGLE30) < 0 ? rayAngle + ANGLE360 : rayAngle;
		
		int offset;													// texture offset
		float wallDistHorizontal, wallDistVertical, distance = .0f;	// Horizontal and vertical wall distances:
		float Xa, Ya;												// moving horizontally (vertically) on grid
		float Ay, Ax;												// wall intersection coordinates
		float tempAx = .0f, tempAy = .0f;							// wall-slice coordinate for computing texture offset
		int projectedSliceHeight;									// this height what you'll see
		int gridX, gridY;											// INTEGER grid coordinates for the intersections
		
		// buffer for frames
		BufferedImage screenBuffer = new BufferedImage(planeWidth,planeHeight,BufferedImage.TYPE_INT_ARGB);
		gb = screenBuffer.createGraphics();
		
		// arrays for the texture pixels and the slice pixels
		// all of the textures has the size of SIZE
		int pixels[] = new int[SIZE];
		int slicePixels[];
		
		int storeyPixels[] = new int[SIZE];
		int storeySlicePixels[];
		boolean isThereStorey;
		int storeyId = -1;
		
		
		// spritecasting		
		int tileIndexHor = 0, tileIndexVer = 0;
		boolean DOWN, RIGHT;
		
		// thin walls
		float thinWallTempHor[] = new float[3];
		float thinWallTempVer[] = new float[3];
		boolean isThereThinWallHor, isThereThinWallVer = false;
		boolean isHorizontalWallInside = false, isVerticalWallInside = false;
		
		// skybox and background
		gb.setColor(Color.GRAY);
		gb.fillRect(0, planeHeight>>1, planeWidth, planeHeight>>1);
		gb.drawImage(skybox.getImage(planeWidth, planeHeight>>1), 0, 0, this);
		
		// Loop from left to right
		for( int raysCasted = 0; raysCasted < planeWidth; raysCasted += angleBetweenRays) {
			
			// START VALUES FOR VARIABLES, DISTANCES...ETC
			
			// default distance
			zbuffer[raysCasted] = Float.MAX_VALUE;
			
			// no thin-wall is the default
			isThereThinWallHor = false;
			isThereThinWallVer = false;
			thinWallTempHor[0] = Float.MAX_VALUE;
			thinWallTempVer[0] = Float.MAX_VALUE;
			
			// default wall height
			int verticalHeight = SIZE, horizontalHeight = SIZE;
			
			// default state is inside
			isHorizontalWallInside = true;
			isVerticalWallInside = true;
			
			// default is no storey
			isThereStorey = false;
			
			///////////////	HORIZONTAL INTERSECTIONS /////////////////////////////////////////
			if( (rayAngle > ANGLE0) && (rayAngle < ANGLE180) ) {	// facing down
				
				// angle > ANGLE 0 && angle < ANGLE 180
				//System.out.println("You're facing down!");
				DOWN = true;
				Ya = SIZE;
				Ay =  ( (( ( (getPlayerY()>>SIZE_LOG) ) )) << SIZE_LOG ) + SIZE ;
				Ax = getPlayerX() + (InvTanTable[rayAngle] * (Ay-getPlayerY()));
			} else {
				// ! (angle > ANGLE 0 && angle < ANGLE 180)
				//System.out.println("You're facing up!");
				DOWN = false;
				Ya = -SIZE;
				Ay = (( (getPlayerY()>>SIZE_LOG) )) << SIZE_LOG;
				Ax = getPlayerX() + (InvTanTable[rayAngle] * (--Ay-getPlayerY()));
				//Ay -= 1.0;
			}
			Xa = xStepTable[rayAngle];			
			while(true) {
				// GRID COORDINATES (ARRAY COORDINATE)
				gridX = (int) Math.floor(Ax / SIZE);
				gridY = (int) Math.floor(Ay / SIZE);
				// CHECK ANGLE
				if( (rayAngle == ANGLE0) ^ (rayAngle == ANGLE180) ) {
					wallDistHorizontal = Float.MAX_VALUE;
					break;
				}			
				// OUT OF GRID ?
				if( ((gridX < 0) ^ (gridX >= mapWidth)) || ((gridY < 0) ^ (gridY >= mapHeight)) ) {
					wallDistHorizontal = Float.MAX_VALUE;
					break;
				}
				// THIN WALL
				if( !isThereThinWallHor && map.isDoor(gridX, gridY) ) {
					if( map.isOpen(gridX, gridY) ) {
						thinWallTempHor[0] = Math.abs( (Ax-getPlayerX())*InvCosTable[rayAngle] );
						thinWallTempHor[1] = DOOR_OPENED;
						thinWallTempHor[2] = (float) Math.floor(Ax);
						isThereThinWallHor = true;
					} else {
						wallDistHorizontal = Math.abs( (Ax-getPlayerX())*InvCosTable[rayAngle] );
						tileIndexHor = DOOR_CLOSED;
						tempAx = (float) Math.floor(Ax);
						zbuffer[raysCasted] = (float) Math.sqrt( (getPlayerX()-( (gridX<<SIZE_LOG) + (SIZE>>1) ))*(getPlayerX()-( (gridX<<SIZE_LOG) + (SIZE>>1) ))+
								 								 (getPlayerY()-( (gridY<<SIZE_LOG) + (SIZE>>1) ))*(getPlayerY()-( (gridY<<SIZE_LOG) + (SIZE>>1) )) );
						break;
					}
				}
				// NORMAL WALL
				if( map.isWall(gridX, gridY) ) {
					
					// STOREY
					storeyId = map.getStorey(gridX, gridY);
					if( storeyId != -1 ) {
						isThereStorey = true;
					}
					
					if(map.isOutside(gridX, gridY))
						isHorizontalWallInside = false;
					
					wallDistHorizontal = Math.abs( (Ax-getPlayerX())*InvCosTable[rayAngle] );
					tileIndexHor = map.texMap[gridY*mapWidth+gridX];
					tempAx = (float) Math.floor(Ax);
					zbuffer[raysCasted] = (float) Math.sqrt( (getPlayerX()-( (gridX<<SIZE_LOG) + (SIZE>>1) ))*(getPlayerX()-( (gridX<<SIZE_LOG) + (SIZE>>1) ))+
							 								 (getPlayerY()-( (gridY<<SIZE_LOG) + (SIZE>>1) ))*(getPlayerY()-( (gridY<<SIZE_LOG) + (SIZE>>1) )) );
					
					horizontalHeight = map.getHeight(gridX, gridY);
					
					break;
				}
				// NEXT STEP
				Ax += Xa;
				Ay += Ya;
			} // while(true)			
		    //////////////////////////////////////////////////////////////////////////////////
			
			///////////////	VERTICAL INTERSECTIONS ///////////////////////////////////////////
			if( (rayAngle < ANGLE90) ^ (rayAngle > ANGLE270) ) {	// facing right
				RIGHT = true;
				Xa = SIZE;
				Ax = ((getPlayerX()>>SIZE_LOG) << SIZE_LOG) + SIZE;
				Ay = getPlayerY() + (tanTable[rayAngle] * (Ax - getPlayerX()));
			} else {												// facing left
				RIGHT = false;
				Xa = -SIZE;
				Ax = ((getPlayerX()>>SIZE_LOG) << SIZE_LOG);
				Ay = getPlayerY() + (tanTable[rayAngle] * (--Ax - getPlayerX()));
				//Ax -= 1.0;
			}
			Ya = yStepTable[rayAngle];
			while(true) {
				// GRID COORDINATES (ARRAY COORDINATE)
				gridX = (int) Math.floor(Ax / SIZE);
				gridY = (int) Math.floor(Ay / SIZE);
				// CHECK ANGLE
				if( (rayAngle == ANGLE90) ^ (rayAngle == ANGLE270) ) {
					wallDistVertical = Float.MAX_VALUE;
					break;
				}
				// OUT OF GRID ?
				if( ((gridX < 0) ^ (gridX >= mapWidth)) || ((gridY < 0) ^ (gridY >= mapHeight)) ) {
					wallDistVertical = Float.MAX_VALUE;
					break;
				}
				// THIN WALL
				if( !isThereThinWallVer && map.isDoor(gridX, gridY) ) {
					if( map.isOpen(gridX, gridY) ) {
						thinWallTempVer[0] = Math.abs( (Ay-getPlayerY())*InvSinTable[rayAngle] );
						thinWallTempVer[1] = DOOR_OPENED;
						thinWallTempVer[2] = Ay;
						isThereThinWallVer = true;
					} else {
						wallDistVertical = Math.abs( (Ay-getPlayerY())*InvSinTable[rayAngle] );
						tileIndexVer = DOOR_CLOSED;
						tempAy = Ay;
						zbuffer[raysCasted] = (float) Math.sqrt( (getPlayerX()-( (gridX<<SIZE_LOG) + (SIZE>>1) ))*(getPlayerX()-( (gridX<<SIZE_LOG) + (SIZE>>1) ))+
								 								 (getPlayerY()-( (gridY<<SIZE_LOG) + (SIZE>>1) ))*(getPlayerY()-( (gridY<<SIZE_LOG) + (SIZE>>1) )) );
						break;
					}
				}
				// NORMAL WALL
				if( map.isWall(gridX, gridY) ) {
					
					// STOREY
					storeyId = map.getStorey(gridX, gridY);
					if( storeyId != -1 ) {
						isThereStorey = true;
					}
					
					if(map.isOutside(gridX, gridY))
						isVerticalWallInside = false;
					
					wallDistVertical = Math.abs( (Ay-getPlayerY())*InvSinTable[rayAngle]);
					tileIndexVer = map.texMap[gridY*mapWidth+gridX];	
					tempAy = Ay;
					zbuffer[raysCasted] = (float) Math.sqrt( (getPlayerX()-( (gridX<<SIZE_LOG) + (SIZE>>1) ))*(getPlayerX()-( (gridX<<SIZE_LOG) + (SIZE>>1) ))+
							 								 (getPlayerY()-( (gridY<<SIZE_LOG) + (SIZE>>1) ))*(getPlayerY()-( (gridY<<SIZE_LOG) + (SIZE>>1) )) );
					
					verticalHeight = map.getHeight(gridX, gridY);
					
					break;
				}
				// NEXT STEP
				Ay += Ya;
				Ax += Xa;
			} // while(true)			
		    //////////////////////////////////////////////////////////////////////////////////
			
			///////////////	TEXTURE PIXEL OFFSET /////////////////////////////////////////////
			// HORIZONTAL
			if(wallDistHorizontal < wallDistVertical) {
				distance = wallDistHorizontal/fishEyeCorrectionTable[raysCasted];
				zbuffer[raysCasted] = distance;					
				if(DOWN) {
					offset = 63 - ((((int)(tempAx))) & 63);
					
					manager.getTexture(tileIndexHor).getRGB(offset, 0, 1, SIZE, pixels, 0, 1);
				} else {
					offset = ((((int)(tempAx))) & 63);
					manager.getTexture(tileIndexHor).getRGB(offset, 0, 1, SIZE, pixels, 0, 1);
				}
			// VERTICAL
			} else {
				isHorizontalWallInside = isVerticalWallInside;
				horizontalHeight = verticalHeight;
				distance = wallDistVertical/fishEyeCorrectionTable[raysCasted];
				zbuffer[raysCasted] = distance;					
				if(!RIGHT) {
					offset = 63 - ((((int)(tempAy))) & 63);
					manager.getTexture(tileIndexVer).getRGB(offset, 0, 1, SIZE, pixels, 0, 1);
				} else {
					offset = ((((int)(tempAy))) & 63);
					manager.getTexture(tileIndexVer).getRGB(offset, 0, 1, SIZE, pixels, 0, 1);
				}				
			}
			
			if(isThereStorey) {
				manager.getTexture(storeyId).getRGB(offset, 0, 1, SIZE, storeyPixels, 0, 1);
			}			
		    //////////////////////////////////////////////////////////////////////////////////
			
			// TODO: ezt eltüntetni
			float shaderDistance = distance;
			
			///////////////	WALL DISTANCE AND STARTDRAW VALUE ////////////////////////////////
			// sliceheight relative to SIZE (64 is the common) is mandatory to determine if the wall sunken or risen
			int _64_sliceHeight =  (int) ((getPlayerPaneDist() << SIZE_LOG) / distance);
			// real sliceheight relative to the actual size
			projectedSliceHeight = (int) ((getPlayerPaneDist() * horizontalHeight) / distance);
			
			// "smallest" wall size must be zero
			if(projectedSliceHeight<0)
				projectedSliceHeight = 0;
			
			// smaller than normal walls are sunken while higher than normal walls are risen (relative to SIZE)
			int sunkenOrRisen = projectedSliceHeight - _64_sliceHeight;
			
			// startDraw is the point on the screen where the wall-drawing starts
			int startDraw = ((planeHeight-sunkenOrRisen)>>1)-(projectedSliceHeight>>1);
			if(startDraw>=planeHeight) startDraw = planeHeight-1;			
		    //////////////////////////////////////////////////////////////////////////////////
		    
			floor = new FloorThread(startDraw, projectedSliceHeight, rayAngle, raysCasted, screenBuffer);
			floor.start();
			
			ceiling = new CeilingThread(startDraw, rayAngle, raysCasted, screenBuffer);
			ceiling.start();
//		    /////////////// FLOOR-CASTING ////////////////////////////////////////////////////
//			/*
//			 * 90%-kát magam csináltam meg a matek alapján, maradék 10%-ék (indexelés és angle+rayAngle) innen van: 
//			 * https://www.allegro.cc/forums/thread/374305
//			 * Nem mûködtek a megoldások, ezért egyszerûsítettem rajtuk.
//			 * Stack postom: https://gamedev.stackexchange.com/questions/159285/ray-casting-floor-casting-part-fails
//			 */
//			int floorCastingStartPixel = startDraw+projectedSliceHeight;
//			int x,y;
//			if(floorCastingStartPixel < planeHeight) {
//				for (int i=floorCastingStartPixel; i<=planeHeight-1; i++) {
//					// floor distance
//					distance = ((float) (((float)PLAYERHEIGHT / (i-(planeHeight>>1)) )* getPlayerPaneDist() ) ) * fishEyeCorrectionTable[raysCasted];
//					
//					// floor-tile coordinate in the world
//					x = ( (int) (distance * (cosTable[rayAngle])) ) + getPlayerX();
//					y = ( (int) (distance * (sinTable[rayAngle])) ) + getPlayerY();
//					
//					// floor-tile coordinate on the grid
//					int mapX = x>>6;
//					int mapY = y>>6;
//					
//					// check if out of grid
//					if(mapX < 0 || mapY < 0 || mapX >= mapWidth || mapY >= mapHeight)
//						continue;
//					
//					// outside or inside
//					// x & 63 = x % 64 and x & 63 = x % 64, texture offset
//					// TODO: x&63 helyett x&(SIZE-1)-el ekvivalens általánosítás
//					if( !map.isOutside(mapX, mapY) ) {
//						if(IS_SHADERS_ON) {
//							screenBuffer.setRGB(raysCasted, i, addFogEffect(tex.textures[map.floorMap[mapY*mapWidth+mapX]].img.getRGB(x&63,y&63),distance));
//						} else {
//							screenBuffer.setRGB(raysCasted, i, tex.getTexImg(map.floorMap[mapY*mapWidth+mapX]).getRGB(x&63,y&63));
//						}
//					} else {
//						screenBuffer.setRGB(raysCasted, i, tex.getTexImg(map.floorMap[mapY*mapWidth+mapX]).getRGB(x&63,y&63));
//					}
//					
//				}
//			}
//			//////////////////////////////////////////////////////////////////////////////////
//			
//			/////////////// CEIL-CASTING ////////////////////////////////////////////////////
//			if(startDraw>=0) {
//				for (int i=0; i<=startDraw; i++) {
//					// ceil distance
//					distance = (float) (Math.abs(((float)PLAYERHEIGHT / (i-(planeHeight>>1)) )* getPlayerPaneDist() )) * fishEyeCorrectionTable[raysCasted];
//					
//					// ceil world coordinates
//					x = ( (int) (distance * (cosTable[rayAngle])) ) + getPlayerX();
//					y = ( (int) (distance * (sinTable[rayAngle])) ) + getPlayerY();
//					
//					// ceil grid coordinates
//					int mapX = x>>6;
//					int mapY = y>>6;
//					
//					// check if outside of grid
//					if(mapX < 0 || mapY < 0 || mapX >= mapWidth || mapY >= mapHeight)
//						continue;
//					
//					// outside or inside
//					// x & 63 = x % 64 and x & 63 = x % 64, texture offset
//					// TODO: x&63 helyett x&(SIZE-1)-el ekvivalens általánosítás
//					// TODO: megjavítani
//					try {
//						if( !map.isOutside(mapX, mapY) ) {
//							if(IS_SHADERS_ON) {
//								screenBuffer.setRGB(raysCasted, i, addFogEffect(tex.getTexImg(map.ceilMap[mapY*mapWidth+mapX]).getRGB(x&63,y&63),distance));
//							} else {
//								screenBuffer.setRGB(raysCasted, i, tex.getTexImg(map.ceilMap[mapY*mapWidth+mapX]).getRGB(x&63,y&63));
//							}						
//						}						
//					} catch (Exception e) {
//						System.out.println("x:"+(x>>6)+"|y:"+(y>>6));
//						e.printStackTrace();
//					};
//				}
//			}
//			//////////////////////////////////////////////////////////////////////////////////
			
			///////////////	SCALING TEXTURE SLICE ////////////////////////////////////////////
			// Scaling function simplified to handle one pixel wide images better. Inlined for better perfomance.			
			if(projectedSliceHeight==0) projectedSliceHeight = 1;
			slicePixels = new int[projectedSliceHeight];
			storeySlicePixels = new int[projectedSliceHeight];
		    int y_ratio = (int)((SIZE<<16)/projectedSliceHeight)+1;
		    for (int i=0;i<projectedSliceHeight;i++) {
		    	if(((i*y_ratio)>>16) < SIZE ) {
		    		// wall
		    		slicePixels[i] = pixels[((i*y_ratio)>>16)] ;
		    		// storey
		    		if(isThereStorey) {
		    			storeySlicePixels[i] = storeyPixels[((i*y_ratio)>>16)];
		    		}
		    	}	
		    }			
		    //////////////////////////////////////////////////////////////////////////////////
		    
		    ///////////////	DRAWING WALL SLICE ///////////////////////////////////////////////
		    if(isHorizontalWallInside && IS_SHADERS_ON) {
	    		slicePixels = addFogEffect(slicePixels,shaderDistance);
		    }
		    
		    for(int wy = startDraw, wy2 = startDraw-projectedSliceHeight, index = 0; 
		    		wy <= startDraw+projectedSliceHeight-1 || wy2 <= startDraw-1; wy++, wy2++, index++) {
		    	if( isThereStorey && wy2 >= 0 && wy2 < planeHeight) {
		    		screenBuffer.setRGB(raysCasted, wy2, storeySlicePixels[index]);
		    	}
		    	if(wy >= 0 && wy < planeHeight) {
		    		screenBuffer.setRGB(raysCasted, wy, slicePixels[index]);
		    	}
		    }
		    /*
		    for(int wy = startDraw, index = 0; wy <= startDraw+projectedSliceHeight-1; wy++, index++) {
		    	if(wy >= 0 && wy < planeHeight) {
		    		screenBuffer.setRGB(raysCasted, wy, slicePixels[index]);
		    	}
		    }
		    */
		    /*
		    for(int wy = startDraw-projectedSliceHeight, index = 0; wy <= startDraw-1; wy++, index++) {
		    	if(wy >= 0 && wy < planeHeight) {
		    		screenBuffer.setRGB(raysCasted, wy, slicePixels[index]);
		    	}
		    }
		    */
			//////////////////////////////////////////////////////////////////////////////////			
			
			///////////////	SCALING AND DRAWING THIN WALL ////////////////////////////////////
			/*
			thinWallTemp...[0] =  DISTANCE FROM PLAYER // Math.abs( (Ax-playerX)*InvCosTable[rayAngle] );
			thinWallTemp...[1] =  TILE TEXTURE INDEX   // DOOR_OPENED;
			thinWallTemp...[2] =  AX or AY             // (float) Math.floor(Ax);	// tempAx
			*/
			if(isThereThinWallVer || isThereThinWallHor) {
				
				if(thinWallTempHor[0] < thinWallTempVer[0]) {
					distance = (int) thinWallTempHor[0]/fishEyeCorrectionTable[raysCasted];
					
					if(DOWN) {
						offset = 63 - ((((int)(thinWallTempHor[2]))) & 63);
					} else {
						offset = ((((int)(thinWallTempHor[2]))) & 63);
					}			
					
				} else {
					distance = (int) thinWallTempVer[0]/fishEyeCorrectionTable[raysCasted];
					
					if(!RIGHT) {
						offset = 63 - ((((int)(thinWallTempVer[2]))) & 63);
					} else {
						offset = ((((int)(thinWallTempVer[2]))) & 63);
					}	
				}
				
				manager.getTexture(map.openedDoor).getRGB(offset, 0, 1, SIZE, pixels, 0, 1);
				
				if(distance < zbuffer[raysCasted]) {					
					projectedSliceHeight = (int) ((getPlayerPaneDist()<<SIZE_LOG) / distance);
					
					if(projectedSliceHeight == 0) projectedSliceHeight = 1;
					
					if(startDraw>=planeHeight) startDraw = planeHeight-1;
					
					startDraw = (planeHeight>>1)-(projectedSliceHeight>>1);
					if(startDraw>=planeHeight) startDraw = planeHeight-1;
					
					// Scaling function simplified to handle one pixel wide images better. Inlined for better perfomance.
					slicePixels = new int[projectedSliceHeight];    
				    y_ratio = (int)((SIZE<<16)/projectedSliceHeight)+1;
				    for (int i=0;i<projectedSliceHeight;i++) {
				    	if( (((i*y_ratio)>>16) < SIZE) )
				    			slicePixels[i] = pixels[((i*y_ratio)>>16)] ;
				    }
				    
				    for(int wy = startDraw, index = 0; wy <= startDraw+projectedSliceHeight-1; wy++, index++) {
				    	if(wy >= 0 && wy < planeHeight && ( (slicePixels[index]>>24) & 0xff ) != 0 ) {
				    		screenBuffer.setRGB(raysCasted, wy, slicePixels[index]);
				    	}
				    }
				}
				
				
			}
			//////////////////////////////////////////////////////////////////////////////////
						
			///////////////	INCREMENTING RAY-ANGLE TO NEXT POSITION //////////////////////////
			rayAngle = (rayAngle+=angleBetweenRays) >= ANGLE360 ? (rayAngle - ANGLE360) : rayAngle;
			//////////////////////////////////////////////////////////////////////////////////
			
		} // for loop; rays from left to right
		
		if(IS_SPRITES_ON)
			castSprites(screenBuffer);
		
		// Preparing the actual frame to be drawn.
		frame = screenBuffer;
	}
		
	/**
	 * Find the screen X coordinate for a sprite on the map.
	 * https://www.allegro.cc/forums/thread/355015
	 * @param spriteX
	 * @param spriteY
	 * @return x screen-coordinate of sprite
	 */
	private final float findXOffsetForSprites(int spriteX, int spriteY) {
		int theta = (player.angle * 60 / planeWidth);
		int fov = getFOV() * 60 / planeWidth;
		
		if(theta < 0) theta += 360;
		if(theta >= 360) theta -= 360;
		
		int xInc = (int) ( (spriteX<<SIZE_LOG) - player.x);
		int yInc = (int) ( (spriteY<<SIZE_LOG) - player.y);
		
		// radian = degree * PI / 180°
		// degree = radian / PI * 180°
		
		float thetaTemp = (float) Math.atan2(yInc,xInc);
		thetaTemp *= 180/Math.PI;
		
		if(thetaTemp < 0) thetaTemp += 360;
		
		// theta - player.angle
		float yTmp = theta + (fov>>1) - thetaTemp;
		if(thetaTemp > 270 && theta < 90) yTmp = theta + (fov>>1) - thetaTemp + 360;
		if(theta > 270 && thetaTemp < 90) yTmp = theta + (fov>>1) - thetaTemp - 360;
		
		float xTmp = (float) (yTmp * planeWidth / (fov+.0) );
		
		return planeWidth-xTmp-32;
	}
	
	/**
	 * Examine the existing sprites and draw slice-by-slice the ones visible in the player's FOV (Field Of View).
	 * 
	 * @param screenBuffer
	 */
	private final void castSprites(BufferedImage screenBuffer) {
		int[] pixels2 = new int[SIZE<<SIZE_LOG];
		int[] pixels3;
		int projectedSliceHeight;
		int onscreenX;
		float distance;
		int temp;
		
		//System.out.println("Spritecounter: "+spritesInActualmap.length);
		for(int i = 0; i < manager.sprites.length; i++) {
			
			onscreenX = (int) findXOffsetForSprites(manager.sprites[i].x, manager.sprites[i].y);
			
			distance = manager.sprites[i].distanceFromPlayer;
			projectedSliceHeight = (int) ((getPlayerPaneDist()<<(SIZE_LOG)) / distance ) % planeHeight;
			
			if( (distance <= 91) || ( (onscreenX+projectedSliceHeight) <= 0) ^ ( (onscreenX) >= planeWidth) )
				continue;
			
			if(projectedSliceHeight<=0)
				continue;
			
			temp = ( (planeHeight>>1)-(projectedSliceHeight>>1) );
			if (temp < 0) temp = 0;
			
			manager.getTexture(manager.sprites[i].texture).getRGB( 0, 0, SIZE, SIZE, pixels2, 0, SIZE);
			pixels3 = new int[projectedSliceHeight*projectedSliceHeight];
			pixels3 = resizePixels(pixels2, SIZE, SIZE, projectedSliceHeight, projectedSliceHeight);
			
			for(int col = 0; col <= projectedSliceHeight; col++) {					
				if( ( (onscreenX >= 0) && (col < planeWidth) && (onscreenX+col < planeWidth) && ( distance < zbuffer[onscreenX+col]) ) ^
					( (onscreenX < 0) && (col < planeWidth) && (onscreenX+col < planeWidth) && ( distance < zbuffer[0+col]) ) ) {
					for (int a=0; a<projectedSliceHeight; a++) {
				    	if( (onscreenX+col < planeWidth) && 
				    		(onscreenX+col >= 0) && 
				    		((temp+a) < planeHeight) && 
				    		(((pixels3[a*projectedSliceHeight+(col%projectedSliceHeight)]>>24)&0xff) != 0) ) {
				    		screenBuffer.setRGB(onscreenX+col, temp+a,pixels3[a*projectedSliceHeight+(col%projectedSliceHeight)]);
				    	}
				    }
				}				
			}
		}
	}
	
	private void drawHelp() {
		String l = this.manager.getHelp();
		gb.setColor(Color.WHITE);
		int y = gb.getFontMetrics().getHeight();
		int x = gb.getFontMetrics().getHeight();
	    for (String line : l.split("\n"))
	        gb.drawString(line, x, y += gb.getFontMetrics().getHeight());
	}
	
	/**
	 * The function handling, calling the casting, drawing functions.
	 */
	public void castGraphic() {
		//castOutside();
		castOutsideOOP();
		if(IS_RAIN_ON && !player.isInside)
			animateRain(gb);
		if(IS_HELP_ON)
			drawHelp();
		msgdisp.showMessage(gb);
		gb.setColor(Color.WHITE);
		gb.drawLine(planeWidth>>1, 0, planeWidth>>1, planeHeight-1);
		showPlayerData();
	}
	
	private void showPlayerData() {
		gb.setColor(Color.BLACK);
		gb.fillRect(5, (planeHeight>>1)+(planeHeight>>2)-15, 130, 60);
		gb.setColor(Color.YELLOW);
		gb.drawString("HP: "+player.getHp(), 10, (planeHeight>>1)+(planeHeight>>2));
		gb.drawString("DP: "+player.getDp(), 10, (planeHeight>>1)+(planeHeight>>2)+20);
		gb.drawString("Active inventory slot: "+player.getActualItemPointer(), 10, (planeHeight>>1)+(planeHeight>>2)+40);		
	}
	
	/**
	 * Draws raindropps on the screen based on the private RainEffect object.
	 * 
	 * @param g
	 */
	private void animateRain(Graphics g) {
		rain.generateDrops();
		g.setColor(Color.GRAY);
		for(int i = 0; i < rain.dropCount-1; i += 2) {
			g.drawLine(rain.drops[i], rain.drops[i+1], rain.drops[i],rain.drops[i+1]+5);
		}
		rain.fall();		
	}
	//////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Depending on the actual rotation of the player it rotate the skybox to the opposite.
	 * @param LEFT
	 */
	public final synchronized void synchSkyboxWithPlayer(boolean LEFT) {
		skybox.rotate(!LEFT,player.rotateSpeed);
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
	
	//////////////////////////////////////////////////////////////////////////////////////////
	
	private class FloorThread implements Runnable {

		private Thread t;
		
		final int startDraw;
		final int projectedSliceHeight;
		final int rayAngle;
		final int raysCasted;
		float distance;
		final BufferedImage screenBuffer;
		
		public FloorThread(final int startDraw, final int projectedSliceHeight, final int rayAngle, final int raysCasted, final BufferedImage screenBuffer) {
			this.startDraw = startDraw;
			this.projectedSliceHeight = projectedSliceHeight;
			this.rayAngle = rayAngle;
			this.raysCasted = raysCasted;
			this.screenBuffer = screenBuffer;
		}
		
		@Override
		public void run() {
			/////////////// FLOOR-CASTING ////////////////////////////////////////////////////
			/*
			 * 90%-kát magam csináltam meg a matek alapján, maradék 10%-ék (indexelés és angle+rayAngle) innen van: 
			 * https://www.allegro.cc/forums/thread/374305
			 * Nem mûködtek a megoldások, ezért egyszerûsítettem rajtuk.
			 * Stack postom: https://gamedev.stackexchange.com/questions/159285/ray-casting-floor-casting-part-fails
			 */
			int floorCastingStartPixel = startDraw+projectedSliceHeight;
			int x,y;
			if(floorCastingStartPixel < planeHeight) {
				for (int i=floorCastingStartPixel; i<=planeHeight-1; i++) {
					// floor distance
					distance = ((float) (((float)PLAYERHEIGHT / (i-(planeHeight>>1)) )* getPlayerPaneDist() ) ) * fishEyeCorrectionTable[raysCasted];
					
					// floor-tile coordinate in the world
					x = ( (int) (distance * (cosTable[rayAngle])) ) + getPlayerX();
					y = ( (int) (distance * (sinTable[rayAngle])) ) + getPlayerY();
					
					// floor-tile coordinate on the grid
					int mapX = x>>6;
					int mapY = y>>6;
					
					// check if out of grid
					if(mapX < 0 || mapY < 0 || mapX >= mapWidth || mapY >= mapHeight)
						continue;
					
					// outside or inside
					// x & 63 = x % 64 and x & 63 = x % 64, texture offset
					// TODO: x&63 helyett x&(SIZE-1)-el ekvivalens általánosítás
					if( !map.isOutside(mapX, mapY) ) {
						if(IS_SHADERS_ON) {
							screenBuffer.setRGB(raysCasted, i, addFogEffect(manager.getTexture(map.texMap[mapY*mapWidth+mapX]).getRGB(x&63,y&63),distance));
						} else {
							screenBuffer.setRGB(raysCasted, i, manager.getTexture(map.texMap[mapY*mapWidth+mapX]).getRGB(x&63,y&63));
						}
					} else {
						screenBuffer.setRGB(raysCasted, i, manager.getTexture(map.texMap[mapY*mapWidth+mapX]).getRGB(x&63,y&63));
					}
					
				}
			}
			//////////////////////////////////////////////////////////////////////////////////
		}
		
		public void start() {
			if (t == null) {
				t = new Thread (this, "FloorThread");
				t.start();
			}
		}
		
	}
	
	private class CeilingThread implements Runnable {

		private Thread t;
		
		final int startDraw;
		final int rayAngle;
		final int raysCasted;
		float distance;
		final BufferedImage screenBuffer;
		
		public CeilingThread(final int startDraw, final int rayAngle, final int raysCasted, final BufferedImage screenBuffer) {
			this.startDraw = startDraw;
			this.rayAngle = rayAngle;
			this.raysCasted = raysCasted;
			this.screenBuffer = screenBuffer;
		}
		
		@Override
		public void run() {
			/////////////// CEIL-CASTING ////////////////////////////////////////////////////
			if(startDraw>=0) {
				int x,y;
				for (int i=0; i<=startDraw; i++) {
					// ceil distance
					distance = (float) (Math.abs(((float)PLAYERHEIGHT / (i-(planeHeight>>1)) )* getPlayerPaneDist() )) * fishEyeCorrectionTable[raysCasted];
					
					// ceil world coordinates
					x = ( (int) (distance * (cosTable[rayAngle])) ) + getPlayerX();
					y = ( (int) (distance * (sinTable[rayAngle])) ) + getPlayerY();
					
					// ceil grid coordinates
					int mapX = x>>6;
					int mapY = y>>6;
					
					// check if outside of grid
					if(mapX < 0 || mapY < 0 || mapX >= mapWidth || mapY >= mapHeight)
						continue;
					
					// outside or inside
					// x & 63 = x % 64 and x & 63 = x % 64, texture offset
					// TODO: x&63 helyett x&(SIZE-1)-el ekvivalens általánosítás
					// TODO: megjavítani
					try {
						if( !map.isOutside(mapX, mapY) ) {
							if(IS_SHADERS_ON) {
								screenBuffer.setRGB(raysCasted, i, addFogEffect(manager.getTexture(map.ceiling).getRGB(x&63,y&63),distance));
							} else {
								screenBuffer.setRGB(raysCasted, i, manager.getTexture(map.ceiling).getRGB(x&63,y&63));
							}						
						}						
					} catch (Exception e) {
						System.out.println("x:"+(x>>6)+"|y:"+(y>>6));
						e.printStackTrace();
					};
				}
			}
			//////////////////////////////////////////////////////////////////////////////////
		}
		
		public void start() {
			if (t == null) {
				t = new Thread (this, "CeilingThread");
				t.start();
			}
		}
		
	}
	
}
