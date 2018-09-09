package hu.emanuel.jeremi.antitower.world;

import static hu.emanuel.jeremi.antitower.common.Tile64.INSIDE;
import static hu.emanuel.jeremi.antitower.common.Tile64.OUTSIDE;
import static hu.emanuel.jeremi.antitower.common.Tile64.VIRTUAL;
import static hu.emanuel.jeremi.antitower.common.Tile64.SIZE_LOG;

import hu.emanuel.jeremi.antitower.entity.Enemy;
import hu.emanuel.jeremi.antitower.entity.Sprite;
import hu.emanuel.jeremi.antitower.entity.ToggleDoor;
import hu.emanuel.jeremi.antitower.save_load.TowLoader.LevelData;

public class MapData2 {
	
	public int width = 26, height = 24;
	
	public Sprite[] spritesFromMap;
	public Enemy[] enemiesFromMap;
	public ToggleDoor[] doors;
	
	public int texMap[];
	public int insideMap[];
	public int floorMap[];
	public int ceilMap[];
	public int heightMap[];
	public int storeyMap[];
	
	public MapData2(ToggleDoor[] doors, Sprite[] spritesFromMap, Enemy[] enemiesFromMap, LevelData d) {
		loadLevel(doors,spritesFromMap,enemiesFromMap,d);
	}
	
	public void loadLevel(ToggleDoor[] doors, Sprite[] spritesFromMap, Enemy[] enemiesFromMap, LevelData d) {
		this.doors = doors;
		this.spritesFromMap = spritesFromMap;
		this.enemiesFromMap = enemiesFromMap;
		
		processMapCells(d);
	}	
	
	private void processMapCells(LevelData d) {
		final int size = d.w * d.h;
		
		this.width = d.w;
		this.height = d.h;
		
		texMap = new int[size];
		insideMap = new int[size];
		floorMap = new int[size];
		ceilMap = new int[size];
		heightMap = new int[size];
		storeyMap = new int[size];		
		
		for(int i = 0; i < size; i++) {
			texMap[i] = d.texMap[i];
			insideMap[i] = d.insideMap[i];
			floorMap[i] = d.floorMap[i];
			ceilMap[i] = d.ceilingMap[i];
			heightMap[i] = d.heightMap[i];
			storeyMap[i] = d.storeyMap[i];
		}
	}
	
	public void printMatrix(int[] matrix, int width) {
		for (int i = 0; i < matrix.length; i++) {
			System.out.print(matrix[i]);
			if (((i+1)%width == 0)) {
				System.out.println();
			}
		}
	}
	
	
	public final boolean isWall(final int x, final int y) {
		return ( (texMap[y*width+x] != INSIDE) && (texMap[y*width+x] != OUTSIDE) && (texMap[y*width+x] != VIRTUAL) );
	}
	
	public final boolean isVirtual(final int x, final int y) {
		return ( (texMap[y*width+x] == VIRTUAL) );
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
		if( ((mapY*width+mapX) < 0) ^ ((mapY*width+mapX) >= width*height) )
			return true;
		return insideMap[mapY*width+mapX] == 0;
	}
	
	public final int getHeight(int x, int y) {
		return heightMap[y*width+x];
	}
	
	public final int getStorey(int x, int y) {
		int temp = storeyMap[y*width+x];
		if(temp > 2) {
			return temp;
		} else {
			return -1;
		}
	}
	
	public final boolean isCollision(int world_x, int world_y, float xd, float yd) {
		return isPathWay(
				((int)((world_x+xd))>>SIZE_LOG), 
				((int)((world_y+yd))>>SIZE_LOG)
		);
	}
}
