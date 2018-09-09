package hu.emanuel.jeremi.antitower.entity;

import static hu.emanuel.jeremi.antitower.common.Tile64.SIZE;
import static hu.emanuel.jeremi.antitower.common.Tile64.SIZE_LOG;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import hu.emanuel.jeremi.antitower.entity.item.AssumableItem;
import hu.emanuel.jeremi.antitower.entity.item.Item;
import hu.emanuel.jeremi.antitower.entity.item.ItemType;
import hu.emanuel.jeremi.antitower.graphic.Graphic;
import hu.emanuel.jeremi.antitower.graphic.TextureLibrary;
import hu.emanuel.jeremi.antitower.i18n.ResourceProvider;
import hu.emanuel.jeremi.antitower.message.MessageHandler;
import hu.emanuel.jeremi.antitower.message.MessagePoint;
import hu.emanuel.jeremi.antitower.message.helpmessage.Help;
import hu.emanuel.jeremi.antitower.physics.GamePhysicsHelper;
import hu.emanuel.jeremi.antitower.save_load.TowLoader;
import hu.emanuel.jeremi.antitower.save_load.TowLoader.LevelData;
import hu.emanuel.jeremi.antitower.world.MapData;

public class EntityManager2 implements PlayerWorldConnector {
	
	public ResourceProvider rr;
	public Help h;
	
	TowLoader tower;
	
	public Sprite[] sprites;
	public ToggleDoor[] doors;
	public Enemy[] enemies;
	public AssumableItem[] assumables;	
	public ArrayList<MessagePoint> msgp;
	
	private Player player;
	
	public int planeWidth, planeHeight;
	
	public Graphic renderer;
	
	public MapData map;
	public TextureLibrary texs;
	public MessageHandler msgh;
	
	private String actualLevelFileName;
	
	public EntityManager2(Player player, int planeWidth, int planeHeight, 
						ResourceProvider rr, Help h, TextureLibrary tl, TowLoader tower, Graphic renderer) {
		this.tower = tower;
		
		this.player = player;
		
		texs = tl;
		
		this.rr = rr;
		
		this.planeWidth = planeWidth;
		this.planeHeight = planeHeight;
		
		LoadLevel("level.tow");
		
		msgh = new MessageHandler();
		
		msgp = new ArrayList<>(Arrays.asList(new MessagePoint[] {
				new MessagePoint("Teszt","Bemész egy ajtón...",10,23,11),
		}));
		
		player.addItem(
				new Item(ItemType.ZAPPER,110,10,texs.items_hud[6],texs.items_hud[3],texs.items_hud[0])			
		);
		
		this.h = h;
		
		this.renderer = renderer; 
	}
	
	private void LoadLevel(String name) {
		this.actualLevelFileName = name;
		
		LevelData d = tower.ReadLevel(texs,name);
		
		initEnemies(d.enemies);
		initAssumables(d.items);		
		initInteractives(d.toggledoors);
		initSprites(d.sprites, d.items, d.enemies);
		
		map = new MapData(doors,sprites,enemies,d);
	}
	
	public void reloadActualLevel() {
		LevelData d = tower.ReadLevel(texs,this.actualLevelFileName);
		
		initEnemies(d.enemies);
		initAssumables(d.items);		
		initInteractives(d.toggledoors);
		initSprites(d.sprites, d.items, d.enemies);
		
		map = new MapData(doors,sprites,enemies,d);
	}
	
	public String getHelp() {
		return this.h.getHelp();
	}
	
	/**
	 * 
	 */
	public void makePlayerInteractWithClosestInteractive() {
		Interactive minTemp = null;
		float minDistance = Float.MAX_VALUE;
		float distance = Float.MAX_VALUE;
		for(Interactive i : doors) {
			distance = GamePhysicsHelper.getDistance(i.getMapX(),i.getMapY(),player.x,player.y);
			if(distance <= minDistance) {
				minDistance = distance;
				minTemp = i;
			}
		}
		if(minDistance > 150.0) {
			player.INTERACTING = false;
			return;
		}			
		//if(minDistance < )
		if( !player.interact(minTemp) ) {
			msgh.addMessage("@",rr.get("need_keycard"),1,1);
		} else {
			msgh.addMessage("@",rr.get("keycard_accepted"),1,1);
		}
	}
	
	/**
	 * 
	 */
	private void initEnemies(Enemy[] en) {
//		enemies = new Enemy[] {
//			new Enemy(10,20,20,true,Enemy.EnemyType.BZZZZ_TOWER,texs.sprites,10),
//			new Enemy(11,15,20,true,Enemy.EnemyType.RIFLE_TOWER,texs.sprites,10),
//			new Enemy(10,16,20,true,Enemy.EnemyType.BZZZZ_TOWER,texs.sprites,10),
//			new Enemy(12,17,20,true,Enemy.EnemyType.SCOPE_TOWER,texs.sprites,10),			
//			new Enemy(11,18,20,true,Enemy.EnemyType.RIFLE_TOWER,texs.sprites,10),
//			new Enemy(12,19,20,true,Enemy.EnemyType.SCOPE_TOWER,texs.sprites,10),
//		};
		enemies = new Enemy[en.length];
		for (int i = 0; i < enemies.length; i++) {
			enemies[i] = en[i];
		}
	}
	
	/**
	 * 
	 */
	private void initAssumables(AssumableItem[] as) {		
//		assumables = new AssumableItem[] {
//			new AssumableItem(ItemType.KEY_CARD,111,111,texs.items_hud[7],texs.items_hud[4],texs.items_hud[1],20,3),
//			new AssumableItem(ItemType.SHIELD,5243,100,texs.items_hud[8],texs.items_hud[5],texs.items_hud[2],24,5),
//		};
		assumables = new AssumableItem[as.length];
		for (int i = 0; i < assumables.length; i++) {
			assumables[i] = as[i];
		}
	}
	
	/**
	 * 
	 */
	private void initSprites(Sprite[] sp, AssumableItem[] as, Enemy[] en) {
//		int amount = 16;
//		sprites = new Sprite[amount];
//		int i;
//		for(i = 0; i < enemies.length; i++) {
//			sprites[i] = enemies[i].frames;
//		}
//		sprites[i++] = new Sprite(texs.textures[16],24,1,10);
//		sprites[i++] = new Sprite(texs.textures[16],24,2,11);
//		sprites[i++] = new Sprite(texs.textures[16],22,2,12);
//		sprites[i++] = new Sprite(texs.textures[16],23,4,13);
//		sprites[i++] = new Sprite(texs.textures[16],22,22,15);
//		sprites[i++] = new Sprite(texs.textures[15],18,4,15);
//		sprites[i++] = new Sprite(texs.textures[15],20,2,15);
//		sprites[i++] = new Sprite(texs.textures[15],17,1,15);
//		sprites[i++] = new Sprite(assumables[0].sprite,assumables[0].x,assumables[0].y,assumables[0].id);
//		sprites[i++] = new Sprite(assumables[1].sprite,assumables[1].x,assumables[1].y,assumables[1].id);
//		/*
//		sprites = new Sprite[] {
//			// TODO: enemies bevitelre [initSprites] általánosabb megoldás kell
//			new Sprite(texs.textures[16],24,1,10),
//			new Sprite(texs.textures[16],24,2,11),
//			new Sprite(texs.textures[16],22,2,12),
//			new Sprite(texs.textures[16],23,4,13),
//			new Sprite(texs.textures[16],22,22,15),
//			enemies[0].frames,
//			enemies[1].frames,
//			enemies[2].frames,
//			enemies[3].frames,
//			enemies[4].frames,
//			enemies[5].frames,
//			new Sprite(assumables[0].sprite,assumables[0].x,assumables[0].y,assumables[0].id),
//		};
//		*/
		sprites = new Sprite[sp.length + as.length + en.length];
		int i;
		// Phase 0
		for (i = 0; i < sp.length; i++) {
			sprites[i] = sp[i];
		}
		// Phase 1
		for ( ; i < sp.length + as.length; i++) {
			sprites[i] = new Sprite(as[i - sp.length].sprite, as[i - sp.length].x, as[i - sp.length].y,
									as[i - sp.length].id);
		}
		// Phase 2
		for ( ; i < sp.length + as.length + en.length; i++) {
			sprites[i] = en[i - sp.length - as.length].frames;
		}
	}
	
	/**
	 * 
	 */
	private void initInteractives(ToggleDoor[] in) {
//		doors = new ToggleDoor[] {
//			new ToggleDoor(texs.textures[18],texs.textures[19],23,11,111,true)
//		};
		doors = new ToggleDoor[in.length];
		for (int i = 0; i < in.length; i++) {
			doors[i] = in[i];
		}
	}
	
	/**
	 * 
	 */
	public void updateDistBetweenPlayerAndSprites() {
		for(int i = 0; i < sprites.length; i++) {
			sprites[i].distanceFromPlayer = 
					GamePhysicsHelper.getDistance(sprites[i].x,sprites[i].y,player.x,player.y);
		}
		sortSpritesByDistanceAscending();
	}
	
	/**
	 * 
	 */
	public void sortSpritesByDistanceAscending() {
		if(sprites.length == 0)
			return;
		
		Sprite temp = sprites[0];
		
		for(int i = 0; i < sprites.length-1; i++)
			for(int j = 0; j < sprites.length-i-1; j++ ) {
				if(sprites[j].distanceFromPlayer < sprites[j+1].distanceFromPlayer) {
					temp = sprites[j];
					sprites[j] = sprites[j+1];
					sprites[j+1] = temp;
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
		if(enemies == null || enemies.length == 0)
			return false;
		for(Enemy en : enemies) {
			//System.out.println("x:"+x+"|y:"+y+"|enemy x:"+en.x+"|enemy y:"+en.y);
			if( en != null && x == en.x && y == en.y ) {
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
		if(enemies == null || enemies.length == 0)
			return false;
		
		int tempX = 0, tempY = 0;
		int angle = 0;
		int sight = 200;
		for(Enemy en : enemies) {
			if( !en.destroyed ) {
				tempX = en.x * SIZE + (SIZE>>1);
				tempY = en.y * SIZE + (SIZE>>1);
				
				angle = (int)(GamePhysicsHelper.rotatePlayerAngleToTarget
						( tempX, tempY, player.x, player.y, player.angle, planeWidth, player.FOV ));
				
				tempX = player.x;
				tempY = player.y;
				
				for(int i = 0; i < sight; i++) {
					tempX += Math.cos(GamePhysicsHelper.toCustomRad(angle, 640*3)) * 10;
					tempY += Math.sin(GamePhysicsHelper.toCustomRad(angle, 640*3)) * 10;
					
					if(map.isWall(tempX>>SIZE_LOG, tempY>>SIZE_LOG)) {
								return false;
					}
					
					if( (int)Math.floor(tempX>>SIZE_LOG) == en.x && 
						(int)Math.floor(tempY>>SIZE_LOG) == en.y ) {
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
		if(msgp.size() == 0)
			return;
		for (Iterator<MessagePoint> iterator = msgp.iterator(); iterator.hasNext(); ) {
			MessagePoint mp = iterator.next();
			if(mp.x == (player.x>>SIZE_LOG) && mp.y == (player.y>>SIZE_LOG) ) {
				//System.out.println(mp.x+"|"+mp.y+"\n"+(player.x>>SIZE_LOG)+"|"+(player.y>>SIZE_LOG));
				msgh.addMessage(mp);
				iterator.remove();
			}	
		}
	}
	
	public void checkAssumableCollision() {
		if(assumables.length == 0)
			return;
		Sprite[] temp = null;
		int l = 0;
		for(int i = 0; i < assumables.length; i++) {
			if(assumables[i] != null) {
				if(assumables[i].x == (player.x>>SIZE_LOG) && assumables[i].y == (player.y>>SIZE_LOG) ) {
					player.addItem(assumables[i]);
					// TODO: -1 id esetén nullpointer exp. messagedisplayerben...megakadályozni
					msgh.addMessage("@",rr.get("player_item_gained"),2,3);
					for(int j = 0; j < sprites.length; j++) {
						if(sprites[j].id == assumables[i].id) {
							temp = new Sprite[sprites.length-1];
							for(int k = 0; k < sprites.length; k++) {
								if(sprites[k].id != assumables[i].id) {
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
		return map.isPathWay(((int)((playerx+dx))>>SIZE_LOG), 
							((int)((playery+dy))>>SIZE_LOG));
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
	public EntityManager2 getManager() {
		return this;
	}

}
