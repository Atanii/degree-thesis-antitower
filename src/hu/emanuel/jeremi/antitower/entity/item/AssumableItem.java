package hu.emanuel.jeremi.antitower.entity.item;

public class AssumableItem extends Item {
	
	public int x,y;
	
	public AssumableItem(ItemType type, int id, int value, int x, int y) {
		super(type, id, value);
		this.x = x;
		this.y = y;
	}
	
	public final static int[][] KC_Z_S_ids = {
			{7,4,1},
			{6,3,0},
			{8,5,2}	
	};	
}
