package hu.emanuel.jeremi.antitower.save_load;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.StringTokenizer;

import javax.swing.JOptionPane;

import hu.emanuel.jeremi.antitower.entity.Enemy;
import hu.emanuel.jeremi.antitower.entity.Enemy.EnemyType;
import hu.emanuel.jeremi.antitower.entity.EntityManager;
import hu.emanuel.jeremi.antitower.entity.Sprite;
import hu.emanuel.jeremi.antitower.entity.ToggleDoor;

/**
 * 
 * This class loads the levels from the .tow files.
 * 
 * @author Jeremi
 *
 */
public final class TowHandler {
	
	public EntityManager gui;
	
	public TowHandler(EntityManager gui) {
		this.gui = gui;
	}
	
	public TowHandler() {
	}
	
	private class BlankMapException extends Exception {
		
	}
	/*
	public void SaveLevel(final String PATH) {
		
		try(
			FileWriter fw = new FileWriter(PATH);
			BufferedWriter bw = new BufferedWriter(fw);
			PrintWriter pw = new PrintWriter(bw);
		) {
			System.out.println("<<< SAVING: " + PATH + " >>>");
			
			pw.println("MAP_VERSION");
			
			// MAP VERSION
			pw.print(Integer.toString(3) + ';');
			
			pw.print('\n');
			pw.println("PLAYER_DATA");
			
			// PLAYER DATA
			pw.print(Integer.toString(gui.playerX) + ',' + Integer.toString(gui.playerY) + ';');
			
			pw.print('\n');
			pw.println("USED_TEXTURE_PACK");
			
			// USED TEXTURE PACK
			pw.print(Integer.toString(gui.chosenTexturePack) + ';');
			
			pw.print('\n');
			pw.println("MAP_SIZE");
			
			// MAP SIZE (in gridcount)
			pw.print(Integer.toString(gui.mapW) + ',' + Integer.toString(gui.mapH) + ';');
			
			pw.print('\n');
			pw.println("CEILING");
			
			// CEILING
			pw.print(Integer.toString(gui.chosenCeiling) + ';');
			
			pw.print('\n');
			pw.println("MAP_CELLS");
			
			// MAP CELLS
			// x y fw height inside virtual storey //
			for(CellData d : walls) {
				pw.print(
					Integer.toString(d.x) + ',' + 
					Integer.toString(d.y) + ',' +
					Integer.toString(d.fw) + ',' +
					Integer.toString(d.height) + ',' +
					Integer.toString(d.inside) + ',' +
					Integer.toString(d.virtual) + ',' +
					Integer.toString(d.storey) + ';'
				);
			}
			
			pw.print('\n');
			pw.println("DOORS");
			
			// DOORS
			// x y closed opened id value //
			pw.print(Integer.toString(doors.size()) + ';');
			System.out.println("Doors: " + doors.size());
			if (doors.size() > 0) {
				pw.print('\n');
				
				pw.print(Integer.toString(gui.chosenDoorClosed) + ',');
				pw.print(Integer.toString(gui.chosenDoorOpened) + ';');
				
				for(DoorData d : doors) {
					pw.print(
						Integer.toString(d.x) + ',' + 
						Integer.toString(d.y) + ',' +
						Integer.toString(d.id) + ',' +
						Integer.toString(d.value) + ';'
					);
				}
			}
			
			pw.print('\n');
			pw.println("SPRITES");
			
			// SPRITES
			// x y tile id //
			pw.print(Integer.toString(sprites.size()) + ';');
			pw.print('\n');
			for(SpriteData d : sprites) {
				pw.print(
					Integer.toString(d.x) + ',' + 
					Integer.toString(d.y) + ',' +
					Integer.toString(d.texture()) + ',' +
					Integer.toString(d.id) + ';'
				);
			}
			
			pw.print('\n');
			pw.println("ENEMIES");
			
			// ENEMIES
			// x y id type //
			pw.print(Integer.toString(enemies.size()) + ';');
			pw.print('\n');
			for(EnemyData d : enemies) {
				pw.print(
					Integer.toString(d.x) + ',' + 
					Integer.toString(d.y) + ',' +
					Integer.toString(d.id) + ',' +
					Integer.toString(d.type) + ';'
				);
			}
			
			pw.print('\n');
			pw.println("ITEMS");
			
			// ITEMS
			// x y _tile_ id value type //
			pw.print(Integer.toString(items.size()) + ';');
			pw.print('\n');
			for(ItemData d : items) {
				pw.print(
					Integer.toString(d.x) + ',' + 
					Integer.toString(d.y) + ',' +
					Integer.toString(d.id) + ',' +
					Integer.toString(d.value) + ',' +
					Integer.toString(d.type) + ';'
				);
			}
			
			pw.print('\n');
			pw.println("MESSAGES");
			
			// MESSAGES
			// x y time id sendercode stringCode //
			pw.print(Integer.toString(messages.size()) + ';');
			pw.print('\n');
			for(MessageData d : messages) {
				pw.print(
					Integer.toString(d.x) + ',' + 
					Integer.toString(d.y) + ',' +
					Integer.toString(d.time) + ',' +
					Integer.toString(d.id) + ',' +
					Integer.toString(d.senderCode) + ',' +
					Integer.toString(d.stringCode) + ';'
				);
			}
			
			System.out.println("<<< SAVED: " + PATH + " >>>");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	*/
	public void LoadLevel(final String PATH, boolean fromResource) {
		if (fromResource) {
			try(
				Scanner sc = new Scanner(getClass().getResource("/res/" + PATH).openStream());
			) {
				System.out.println("<<< LOADING: " + PATH + " >>>");
				
				int current = 0;
				StringTokenizer tokenizer = null;
				sc.useDelimiter(";");
				
				sc.nextLine();
				
				// MAP VERSION
				tokenizer = new StringTokenizer(sc.next(), ",");
				System.out.println("<<< Map version: v" + Integer.parseInt(tokenizer.nextToken()) + " >>>");
				
				sc.nextLine();
				sc.nextLine();
							
				// PLAYER DATA
				tokenizer = new StringTokenizer(sc.next(), ",");
				gui.playerX = Integer.parseInt(tokenizer.nextToken());
				gui.playerY = Integer.parseInt(tokenizer.nextToken());
				System.out.println("<<< Player X: " + gui.playerX + " | " + "Y: " + gui.playerY + " >>>");
				
				sc.nextLine();
				sc.nextLine();
				
				// USED TEXTURE PACK
				tokenizer = new StringTokenizer(sc.next(), ",");
				gui.map.pack = Integer.parseInt(tokenizer.nextToken());
				System.out.println("<<< Texture pack: " + gui.map.pack + " >>>");
				
				sc.nextLine();
				sc.nextLine();
				
				// MAP SIZE (in gridcount)
				tokenizer = new StringTokenizer(sc.next(), ",");
				gui.map.width = Integer.parseInt(tokenizer.nextToken());
				gui.map.height = Integer.parseInt(tokenizer.nextToken());
				System.out.println("<<< Width: " + gui.map.width + " | " + "Height: " + gui.map.height + " >>>");
				
				if (gui.map.width * gui.map.height == 0) {
					throw new BlankMapException();
				}
				
				sc.nextLine();
				sc.nextLine();
				
				// CEILING
				tokenizer = new StringTokenizer(sc.next(), ",");
				gui.map.ceiling = Integer.parseInt(tokenizer.nextToken());			
				System.out.println("<<< Ceiling: " + gui.map.ceiling + " >>>");
				
				sc.nextLine();
				sc.nextLine();
				
				// MAP CELLS
				// x y inside fw height virtual storey //
				// int x, int y, int fw, int height, int inside, int virtual, int storey
				
				// int oneDindex = (row * length_of_row) + column; // Indexes
				
				int size = gui.map.width * gui.map.height;
				
				gui.map.texMap = new int[size];
				gui.map.heightMap = new int[size];
				gui.map.insideMap = new int[size];
				gui.map.storeyMap = new int[size];
				int virtualMap[] = new int[size];
				
				for(int i = 0; i < size; i++) {
					tokenizer = new StringTokenizer(sc.next(), ",");
					
					int x, y;
					
					x = Integer.parseInt(tokenizer.nextToken());
					y = Integer.parseInt(tokenizer.nextToken());
					
					gui.map.texMap[(y * gui.map.width) + x] = Integer.parseInt(tokenizer.nextToken());
					gui.map.heightMap[(y * gui.map.width) + x] = Integer.parseInt(tokenizer.nextToken());
					gui.map.insideMap[(y * gui.map.width) + x] = Integer.parseInt(tokenizer.nextToken());
					virtualMap[(y * gui.map.width) + x] = Integer.parseInt(tokenizer.nextToken());
					gui.map.storeyMap[(y * gui.map.width) + x] = Integer.parseInt(tokenizer.nextToken());
				}
				
				sc.nextLine();
				sc.nextLine();
				
				// DOORS
				// x y closed opened id value //
				tokenizer = new StringTokenizer(sc.next(), ",");
				current = Integer.parseInt(tokenizer.nextToken());
				System.out.println("<<< Doors: " + current + " >>>");
				
				gui.doors = new ToggleDoor[current];
				gui.map.doors = new ToggleDoor[current];
				
				if (current > 0) {
					sc.nextLine();
					
					gui.map.closedDoor = Integer.parseInt(tokenizer.nextToken());
					gui.map.openedDoor = Integer.parseInt(tokenizer.nextToken());
					
					for(int i = 0; i < current; i++) {
						tokenizer = new StringTokenizer(sc.next(), ",");
						
						int x = Integer.parseInt(tokenizer.nextToken());
						int y = Integer.parseInt(tokenizer.nextToken());
						int id = Integer.parseInt(tokenizer.nextToken());
						int key = Integer.parseInt(tokenizer.nextToken());
						
						gui.doors[i] = new ToggleDoor(
							gui.map.closedDoor,
							gui.map.openedDoor,
							x, y,
							key,
							true
						);
					}
				}
				
				sc.nextLine();
				sc.nextLine();
				
				// SPRITES
				// number; texture-id, x, y, id //
				tokenizer = new StringTokenizer(sc.next(), ",");
				current = Integer.parseInt(tokenizer.nextToken());
				
				gui.sprites = new Sprite[current];
				
				if (current > 0) {
					sc.nextLine();
					for(int i = 0; i < current; i++) {
						tokenizer = new StringTokenizer(sc.next(), ",");
						
						int x = Integer.parseInt(tokenizer.nextToken());
						int y = Integer.parseInt(tokenizer.nextToken());
						int tex = Integer.parseInt(tokenizer.nextToken());
						int id = Integer.parseInt(tokenizer.nextToken());
						
						gui.sprites[i] = new Sprite(
							tex, x, y, id
						);
					}
				}
				
				sc.nextLine();
				sc.nextLine();
				/*
				// ENEMIES
				// x y tile id type //
				tokenizer = new StringTokenizer(sc.next(), ",");
				current = Integer.parseInt(tokenizer.nextToken());
				
				gui.enemies = new Enemy[current];
				
				if (current > 0) {
					sc.nextLine();
					for(int i = 0; i < current; i++) {
						tokenizer = new StringTokenizer(sc.next(), ",");
						
						int x = Integer.parseInt(tokenizer.nextToken());
						int y = Integer.parseInt(tokenizer.nextToken());
						int id = Integer.parseInt(tokenizer.nextToken());
						int type = Integer.parseInt(tokenizer.nextToken());
						
						gui.enemies[i] = new Enemy(
							id, x, y, true, EnemyType.values()[type]
						);
					}
				}
				*/
				sc.nextLine();
				sc.nextLine();
				/*
				// ITEMS
				// x y tile id value type //
				tokenizer = new StringTokenizer(sc.next(), ",");
				current = Integer.parseInt(tokenizer.nextToken());
				if (current > 0) {
					sc.nextLine();
					for(int i = 0; i < current; i++) {
						tokenizer = new StringTokenizer(sc.next(), ",");
						
						d.items.add( new ItemData(
							Integer.parseInt(tokenizer.nextToken()),
							Integer.parseInt(tokenizer.nextToken()),
							Integer.parseInt(tokenizer.nextToken()),
							Integer.parseInt(tokenizer.nextToken()),
							Integer.parseInt(tokenizer.nextToken())
						));
					}
				}
				
				sc.nextLine();
				sc.nextLine();
				
				// MESSAGES
				// x y time id sendercode stringCode //
				tokenizer = new StringTokenizer(sc.next(), ",");
				current = Integer.parseInt(tokenizer.nextToken());
				if (current > 0) {
					sc.nextLine();
					for(int i = 0; i < current; i++) {
						tokenizer = new StringTokenizer(sc.next(), ",");
						
						d.messages.add( new MessageData(
							Integer.parseInt(tokenizer.nextToken()),
							Integer.parseInt(tokenizer.nextToken()),
							Integer.parseInt(tokenizer.nextToken()),
							Integer.parseInt(tokenizer.nextToken()),
							Integer.parseInt(tokenizer.nextToken()),
							Integer.parseInt(tokenizer.nextToken())
						));
					}
				}
				*/
				System.out.println("<<< LOADED: " + PATH + " >>>");
			} catch (FileNotFoundException e) {
				JOptionPane.showMessageDialog(null, "File not found.");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (BlankMapException e) {
				JOptionPane.showMessageDialog(null, "Invalid map file! There are no map cells to load.");
			}
		} else {
			try(
					Scanner sc = new Scanner(Paths.get(PATH));
				) {
					System.out.println("<<< LOADING: " + PATH + " >>>");
					
					int current = 0;
					StringTokenizer tokenizer = null;
					sc.useDelimiter(";");
					
					sc.nextLine();
					
					// MAP VERSION
					tokenizer = new StringTokenizer(sc.next(), ",");
					System.out.println("<<< Map version: v" + Integer.parseInt(tokenizer.nextToken()) + " >>>");
					
					sc.nextLine();
					sc.nextLine();
								
					// PLAYER DATA
					tokenizer = new StringTokenizer(sc.next(), ",");
					gui.playerX = Integer.parseInt(tokenizer.nextToken());
					gui.playerY = Integer.parseInt(tokenizer.nextToken());
					System.out.println("<<< Player X: " + gui.playerX + " | " + "Y: " + gui.playerY + " >>>");
					
					sc.nextLine();
					sc.nextLine();
					
					// USED TEXTURE PACK
					tokenizer = new StringTokenizer(sc.next(), ",");
					gui.map.pack = Integer.parseInt(tokenizer.nextToken());
					System.out.println("<<< Texture pack: " + gui.map.pack + " >>>");
					
					sc.nextLine();
					sc.nextLine();
					
					// MAP SIZE (in gridcount)
					tokenizer = new StringTokenizer(sc.next(), ",");
					gui.map.width = Integer.parseInt(tokenizer.nextToken());
					gui.map.height = Integer.parseInt(tokenizer.nextToken());
					System.out.println("<<< Width: " + gui.map.width + " | " + "Height: " + gui.map.height + " >>>");
					
					if (gui.map.width * gui.map.height == 0) {
						throw new BlankMapException();
					}
					
					sc.nextLine();
					sc.nextLine();
					
					// CEILING
					tokenizer = new StringTokenizer(sc.next(), ",");
					gui.map.ceiling = Integer.parseInt(tokenizer.nextToken());			
					System.out.println("<<< Ceiling: " + gui.map.ceiling + " >>>");
					
					sc.nextLine();
					sc.nextLine();
					
					// MAP CELLS
					// x y inside fw height virtual storey //
					// int x, int y, int fw, int height, int inside, int virtual, int storey
					
					// int oneDindex = (row * length_of_row) + column; // Indexes
					
					int size = gui.map.width * gui.map.height;
					
					gui.map.texMap = new int[size];
					gui.map.heightMap = new int[size];
					gui.map.insideMap = new int[size];
					gui.map.storeyMap = new int[size];
					int virtualMap[] = new int[size];
					
					for(int i = 0; i < size; i++) {
						tokenizer = new StringTokenizer(sc.next(), ",");
						
						int x, y;
						
						x = Integer.parseInt(tokenizer.nextToken());
						y = Integer.parseInt(tokenizer.nextToken());
						
						gui.map.texMap[(y * gui.map.width) + x] = Integer.parseInt(tokenizer.nextToken());
						gui.map.heightMap[(y * gui.map.width) + x] = Integer.parseInt(tokenizer.nextToken());
						gui.map.insideMap[(y * gui.map.width) + x] = Integer.parseInt(tokenizer.nextToken());
						virtualMap[(y * gui.map.width) + x] = Integer.parseInt(tokenizer.nextToken());
						gui.map.storeyMap[(y * gui.map.width) + x] = Integer.parseInt(tokenizer.nextToken());
					}
					
					sc.nextLine();
					sc.nextLine();
					
					// DOORS
					// x y closed opened id value //
					tokenizer = new StringTokenizer(sc.next(), ",");
					current = Integer.parseInt(tokenizer.nextToken());
					System.out.println("<<< Doors: " + current + " >>>");
					
					gui.doors = new ToggleDoor[current];
					gui.map.doors = new ToggleDoor[current];
					
					if (current > 0) {
						sc.nextLine();
						
						gui.map.closedDoor = Integer.parseInt(tokenizer.nextToken());
						gui.map.openedDoor = Integer.parseInt(tokenizer.nextToken());
						
						for(int i = 0; i < current; i++) {
							tokenizer = new StringTokenizer(sc.next(), ",");
							
							int x = Integer.parseInt(tokenizer.nextToken());
							int y = Integer.parseInt(tokenizer.nextToken());
							int id = Integer.parseInt(tokenizer.nextToken());
							int key = Integer.parseInt(tokenizer.nextToken());
							
							gui.doors[i] = new ToggleDoor(
								gui.map.closedDoor,
								gui.map.openedDoor,
								x, y,
								key,
								true
							);
						}
					}
					
					sc.nextLine();
					sc.nextLine();
					
					// SPRITES
					// number; texture-id, x, y, id //
					tokenizer = new StringTokenizer(sc.next(), ",");
					current = Integer.parseInt(tokenizer.nextToken());
					
					gui.sprites = new Sprite[current];
					
					if (current > 0) {
						sc.nextLine();
						for(int i = 0; i < current; i++) {
							tokenizer = new StringTokenizer(sc.next(), ",");
							
							int x = Integer.parseInt(tokenizer.nextToken());
							int y = Integer.parseInt(tokenizer.nextToken());
							int tex = Integer.parseInt(tokenizer.nextToken());
							int id = Integer.parseInt(tokenizer.nextToken());
							
							gui.sprites[i] = new Sprite(
								tex, x, y, id
							);
						}
					}
					
					sc.nextLine();
					sc.nextLine();
					/*
					// ENEMIES
					// x y tile id type //
					tokenizer = new StringTokenizer(sc.next(), ",");
					current = Integer.parseInt(tokenizer.nextToken());
					
					gui.enemies = new Enemy[current];
					
					if (current > 0) {
						sc.nextLine();
						for(int i = 0; i < current; i++) {
							tokenizer = new StringTokenizer(sc.next(), ",");
							
							int x = Integer.parseInt(tokenizer.nextToken());
							int y = Integer.parseInt(tokenizer.nextToken());
							int id = Integer.parseInt(tokenizer.nextToken());
							int type = Integer.parseInt(tokenizer.nextToken());
							
							gui.enemies[i] = new Enemy(
								id, x, y, true, EnemyType.values()[type]
							);
						}
					}
					*/
					sc.nextLine();
					sc.nextLine();
					/*
					// ITEMS
					// x y tile id value type //
					tokenizer = new StringTokenizer(sc.next(), ",");
					current = Integer.parseInt(tokenizer.nextToken());
					if (current > 0) {
						sc.nextLine();
						for(int i = 0; i < current; i++) {
							tokenizer = new StringTokenizer(sc.next(), ",");
							
							d.items.add( new ItemData(
								Integer.parseInt(tokenizer.nextToken()),
								Integer.parseInt(tokenizer.nextToken()),
								Integer.parseInt(tokenizer.nextToken()),
								Integer.parseInt(tokenizer.nextToken()),
								Integer.parseInt(tokenizer.nextToken())
							));
						}
					}
					
					sc.nextLine();
					sc.nextLine();
					
					// MESSAGES
					// x y time id sendercode stringCode //
					tokenizer = new StringTokenizer(sc.next(), ",");
					current = Integer.parseInt(tokenizer.nextToken());
					if (current > 0) {
						sc.nextLine();
						for(int i = 0; i < current; i++) {
							tokenizer = new StringTokenizer(sc.next(), ",");
							
							d.messages.add( new MessageData(
								Integer.parseInt(tokenizer.nextToken()),
								Integer.parseInt(tokenizer.nextToken()),
								Integer.parseInt(tokenizer.nextToken()),
								Integer.parseInt(tokenizer.nextToken()),
								Integer.parseInt(tokenizer.nextToken()),
								Integer.parseInt(tokenizer.nextToken())
							));
						}
					}
					*/
					System.out.println("<<< LOADED: " + PATH + " >>>");
				} catch (FileNotFoundException e) {
					JOptionPane.showMessageDialog(null, "File not found.");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (BlankMapException e) {
					JOptionPane.showMessageDialog(null, "Invalid map file! There are no map cells to load.");
				}
		}
	}

}
