package hu.emanuel.jeremi.antitower.save_load;

import static hu.emanuel.jeremi.antitower.common.Tile64.INSIDE;
import static hu.emanuel.jeremi.antitower.common.Tile64.OUTSIDE;
import static hu.emanuel.jeremi.antitower.common.Tile64.VIRTUAL;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.StringTokenizer;

import hu.emanuel.jeremi.antitower.entity.Enemy;
import hu.emanuel.jeremi.antitower.entity.Sprite;
import hu.emanuel.jeremi.antitower.entity.ToggleDoor;
import hu.emanuel.jeremi.antitower.entity.Enemy.EnemyType;
import hu.emanuel.jeremi.antitower.entity.item.AssumableItem;
import hu.emanuel.jeremi.antitower.entity.item.ItemType;
import hu.emanuel.jeremi.antitower.graphic.TextureLibrary;
import hu.emanuel.jeremi.antitower.graphic.TextureLibrary.Texture;
import hu.emanuel.jeremi.antitower.i18n.MessageProvider;
import hu.emanuel.jeremi.antitower.message.MessagePoint;

/**
 * 
 * This class loads the levels from the .tow files.
 * 
 * @author Jeremi
 *
 */
public final class TowLoader {

	public TowLoader() {
		// TODO Auto-generated constructor stub
	}
	
	public class LevelData {
		public int[] insideMap;
		public int[] texMap;
		public int[] ceilingMap;
		public int[] floorMap;
		public int[] heightMap;
		public int[] storeyMap;
		public int[] virtualMap;
		public int w, h;
		
		public ToggleDoor[] toggledoors;
		
		public AssumableItem[] items;
		
		public Enemy[] enemies;
		public Sprite[] sprites;
		
		public int playerx, playery;
		
		public MessagePoint[] msg;
		
		public void initMapArrays(int size) {
			insideMap = new int[size];
			texMap = new int[size];
			ceilingMap = new int[size];
			floorMap = new int[size];
			heightMap = new int[size];
			storeyMap = new int[size];
			virtualMap = new int[size];
		}
		
		public void initToggleDoorArray(int size) {
			toggledoors = new ToggleDoor[size];
		}
		
		public void initAssumableItemArray(int size) {
			items = new AssumableItem[size];
		}
		
		public void initEnemyArray(int size) {
			enemies = new Enemy[size];
		}
		
		public void initSpriteArray(int size) {
			sprites = new Sprite[size];
		}
		
		public void initMsgArray(int size) {
			msg = new MessagePoint[size];
		}
	}
	
	public LevelData ReadLevel(TextureLibrary tl, String filename, MessageProvider rr) {
		
		Scanner sc = null;
		LevelData d = new LevelData();
		
		int row, col, length;
		
		try {
			// get file
			URL url = getClass().getResource("/res/" + filename);
			StringTokenizer tokenizer = null;
			int current = 0;
			sc = new Scanner(url.openStream());
			sc.useDelimiter(";");
			
	        // get player coordinate
	        tokenizer = new StringTokenizer(sc.next(), ",");
	        
	        int x = Integer.parseInt(tokenizer.nextToken());
	        int y = Integer.parseInt(tokenizer.nextToken());
	        
	        System.out.println("x: " + x + "\ny: " + y);
	        
	        d.playerx = x;
	        d.playery = y;
	        
			/////////////////////////
			System.out.println("\n");
			/////////////////////////
			
			// get the amount of cells and the cells themself			
			tokenizer = new StringTokenizer(sc.next(), ",");
			
			d.w = col = Integer.parseInt(tokenizer.nextToken());
			d.h = row = Integer.parseInt(tokenizer.nextToken());
	        length = row * col;
	        
	        d.initMapArrays(length);
	        
	        System.out.println("rows: " + row + "\ncols: " + col);
	        	        
	        	        
	        for(int i = 0; i < length; i++) {
	        	tokenizer = new StringTokenizer(sc.next(), ",");
	        	// inside ceiling floor tex height virtual storey //
	        	d.insideMap[i] = Integer.parseInt(tokenizer.nextToken());
	        	d.ceilingMap[i] = Integer.parseInt(tokenizer.nextToken());
	        	d.floorMap[i] = Integer.parseInt(tokenizer.nextToken());
	        	d.texMap[i] = Integer.parseInt(tokenizer.nextToken());
	        	d.heightMap[i] = Integer.parseInt(tokenizer.nextToken());
	        	d.virtualMap[i] = Integer.parseInt(tokenizer.nextToken());
	        	//d.texMap[i] = d.virtualMap[i] == VIRTUAL ? VIRTUAL : d.texMap[i];
	        	d.texMap[i] = ( (d.insideMap[i] == 1) && (d.heightMap[i] == 0) ) ? INSIDE : d.texMap[i];
	        	d.texMap[i] = ( (d.insideMap[i] == 0) && (d.heightMap[i] == 0) ) ? OUTSIDE : d.texMap[i];
	        	d.storeyMap[i] = Integer.parseInt(tokenizer.nextToken());
//	        	for(int j = 0; j < 7; j++) {
//	        		current = Integer.parseInt(tokenizer.nextToken());
//	        		System.out.print(current + "|");
//	        	}
//	        	System.out.print("++"+i+"++");
	        }
	        
	        /////////////////////////
	        System.out.println("\n");
	        /////////////////////////
	        
	        // get the doors
	        tokenizer = new StringTokenizer(sc.next(), ",");
	        current = Integer.parseInt(tokenizer.nextToken());
	        d.toggledoors = new ToggleDoor[current];
	        System.out.println("doors: " + current);
	        
	        for(int i = 0; i < current; i++) {
	        	// egyenlõre még nincs minek lefutnia
	        	System.out.print("+++++");
	        }
	        
			/////////////////////////
			System.out.println("\n");
			/////////////////////////
	        
	        // get the sprites			
	        tokenizer = new StringTokenizer(sc.next(), ",");
	        
	        current = Integer.parseInt(tokenizer.nextToken());
	        
	        d.sprites = new Sprite[current];
//	        int sprite_data;
	        
	        System.out.println("sprites: " + current);
	        
	        for(int i = 0; i < current; i++) {
	        	tokenizer = new StringTokenizer(sc.next(), ",");
	        	// number; texture-id, x, y, id //
	        	d.sprites[i] = new Sprite(
	        			tl.textures[Integer.parseInt(tokenizer.nextToken())],
	        			Integer.parseInt(tokenizer.nextToken()),
	        			Integer.parseInt(tokenizer.nextToken()),
	        			Integer.parseInt(tokenizer.nextToken())
	        	);	        	
//	        	sprite_data = Integer.parseInt(tokenizer.nextToken());
//        		System.out.print(sprite_data + "|");
//        		sprite_data = Integer.parseInt(tokenizer.nextToken());
//        		System.out.print(sprite_data + "|");
//        		sprite_data = Integer.parseInt(tokenizer.nextToken());
//        		System.out.print(sprite_data + "|");
//        		sprite_data = Integer.parseInt(tokenizer.nextToken());
//        		System.out.print(sprite_data + "|");        		
//	        	System.out.print("+++++");
	        }
	        
			/////////////////////////
			System.out.println("\n");
			/////////////////////////
			
			// get the enemies
			tokenizer = new StringTokenizer(sc.next(), ",");
			
			current = Integer.parseInt(tokenizer.nextToken());
			
			d.enemies = new Enemy[current];
			
//			int enemy_data;
			
			System.out.println("enemies: " + current);
			
			// int id, int x, int y, boolean hostile, EnemyType type, Texture spritesheet[], int sheetWidth
			for(int i = 0; i < current; i++) {
				tokenizer = new StringTokenizer(sc.next(), ",");
				// number ; id, x , y, hostile, type //
				d.enemies[i] = new Enemy(
						Integer.parseInt(tokenizer.nextToken()),
						Integer.parseInt(tokenizer.nextToken()),
						Integer.parseInt(tokenizer.nextToken()),
						Integer.parseInt(tokenizer.nextToken()) == 1,
						EnemyType.values()[Integer.parseInt(tokenizer.nextToken())],
						tl.sprites,
						10
				);
//				enemy_data = Integer.parseInt(tokenizer.nextToken());
//				System.out.print(enemy_data + "|");
//				enemy_data = Integer.parseInt(tokenizer.nextToken());
//				System.out.print(enemy_data + "|");
//				enemy_data = Integer.parseInt(tokenizer.nextToken());
//				System.out.print(enemy_data + "|");				
//				System.out.print("+++++");
			}
			
			/////////////////////////
			System.out.println("\n");
			/////////////////////////
			
			// get the items
			tokenizer = new StringTokenizer(sc.next(), ",");
			
			current = Integer.parseInt(tokenizer.nextToken());
			int type = 0;
			
			d.items = new AssumableItem[current];
			
//			int item_data;
			
			System.out.println("items: " + current);
			
			// ItemType type, int id, int value, Texture sprite, Texture icon, Texture overheadImg, int x, int y
			for(int i = 0; i < current; i++) {
				tokenizer = new StringTokenizer(sc.next(), ",");
				// Types: 0 -> key card ; 1 -> zapper ; 2 -> shield //
				// number ; type , id , value , x , y //
				type = Integer.parseInt(tokenizer.nextToken());
				d.items[i] = new AssumableItem(
						ItemType.values()[type],
						
						Integer.parseInt(tokenizer.nextToken()),
						Integer.parseInt(tokenizer.nextToken()),
						tl.items_hud[AssumableItem.KC_Z_S_ids[type][0]],
						tl.items_hud[AssumableItem.KC_Z_S_ids[type][1]],
						tl.items_hud[AssumableItem.KC_Z_S_ids[type][2]],
						Integer.parseInt(tokenizer.nextToken()),
						Integer.parseInt(tokenizer.nextToken())
						
				);
//				item_data = Integer.parseInt(tokenizer.nextToken());
//				System.out.print(item_data + "|");
//				item_data = Integer.parseInt(tokenizer.nextToken());
//				System.out.print(item_data + "|");
//				item_data = Integer.parseInt(tokenizer.nextToken());
//				System.out.print(item_data + "|");
//				item_data = Integer.parseInt(tokenizer.nextToken());
//				System.out.print(item_data + "|");
//				item_data = Integer.parseInt(tokenizer.nextToken());
//				System.out.print(item_data + "|");				
//				System.out.print("+++++");
			}
	        
			/////////////////////////
			System.out.println("\n");
			/////////////////////////
			
			// get the messages
			tokenizer = new StringTokenizer(sc.next(), ",");
			
			current = Integer.parseInt(tokenizer.nextToken());
			
			int msg_time, msg_x, msg_y, msg_senderCode, msg_id, msg_resCode;
			
			System.out.println("messages: " + current);
			
			d.msg = new MessagePoint[current];
			
			for(int i = 0; i < current; i++) {
				tokenizer = new StringTokenizer(sc.next(), ",");
				
				msg_time = Integer.parseInt(tokenizer.nextToken());
				msg_x = Integer.parseInt(tokenizer.nextToken());				
				msg_y = Integer.parseInt(tokenizer.nextToken());
				msg_id = Integer.parseInt(tokenizer.nextToken());
				msg_senderCode = Integer.parseInt(tokenizer.nextToken());
				msg_resCode = Integer.parseInt(tokenizer.nextToken());
				
				d.msg[i] = new MessagePoint(rr.get(msg_senderCode), rr.get(msg_resCode), msg_time, msg_id, msg_x, msg_y);
				
//				System.out.print(msg_time + "|");
//				System.out.print(msg_x + "|");
//				System.out.print(msg_y + "|");
//				System.out.print(msg_resCode + "|");				
//				System.out.print("\n+++++");
			}
			
			sc.close();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return d;		
	}

}
