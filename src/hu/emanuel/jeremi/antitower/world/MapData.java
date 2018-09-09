package hu.emanuel.jeremi.antitower.world;

import static hu.emanuel.jeremi.antitower.common.Tile64.VIRTUAL;
import static hu.emanuel.jeremi.antitower.common.Tile64.SIZE_LOG;

import hu.emanuel.jeremi.antitower.entity.Enemy;
import hu.emanuel.jeremi.antitower.entity.Sprite;
import hu.emanuel.jeremi.antitower.entity.ToggleDoor;

public class MapData {
	
	public int width = 26, height = 24;
	
	public Sprite[] spritesFromMap;
	public Enemy[] enemiesFromMap;
	public ToggleDoor[] doors;
	
	public int texMap[];
	public int insideMap[];
	public int heightMap[];
	
	public int pack, ceiling, closedDoor, openedDoor;
	
	public MapData(ToggleDoor[] doors, Sprite[] spritesFromMap, Enemy[] enemiesFromMap) {
		loadLevel(doors, spritesFromMap, enemiesFromMap);
	}
	
	public MapData() {
		// TODO Auto-generated constructor stub
	}

	public void loadLevel(ToggleDoor[] doors, Sprite[] spritesFromMap, Enemy[] enemiesFromMap) {
		this.doors = doors;
		this.spritesFromMap = spritesFromMap;
		this.enemiesFromMap = enemiesFromMap;
	}
	
	public void printMatrix(int[] matrix, int width) {
		for (int i = 0; i < matrix.length; i++) {
			System.out.print(matrix[i]);
			if (((i + 1) % width == 0)) {
				System.out.println();
			}
		}
	}
	
	public final boolean isWall(final int x, final int y) {
		return ( (heightMap[y * width + x] > 0) && (texMap[y * width + x] != VIRTUAL) );
	}
	
	public final boolean isDoor(final int x, final int y) {
		for(int i = 0; i < doors.length; i++) {
			if( doors[i].x == x && doors[i].y == y )
				return true;
		}
		return false;
	}
	
	public final boolean isOpen(final int x, final int y) {
		for(int i = 0; i < doors.length; i++) {
			if( doors[i].x == x && doors[i].y == y )
				return !doors[i].isClosed;
		}
		return true;
	}
	
	public final boolean isPathWay(int x, int y) {
		return (!isWall(x,y) && isOpen(x,y));
	}
	
	public final boolean isOutside(int mapX, int mapY) {
		if( ((mapY * width + mapX) < 0) ^ ((mapY * width + mapX) >= width * height) )
			return true;
		return insideMap[mapY * width + mapX] == 0;
	}
	
	public final int getHeight(int x, int y) {
		return heightMap[y * width + x];
	}
	
	public final boolean isCollision(int world_x, int world_y, float xd, float yd) {
		return isPathWay(
				((int) ((world_x + xd)) >> SIZE_LOG), 
				((int) ((world_y + yd)) >> SIZE_LOG)
		);
	}
}
