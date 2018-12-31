package hu.emanuel.jeremi.antitower.entity.item;

import hu.emanuel.jeremi.antitower.entity.Sprite;
import java.awt.image.BufferedImage;

public class AssumableItem extends Item {
	
	public int x,y;
	
	public AssumableItem(ItemType type, int id, int value, int x, int y, BufferedImage img) {
		super(type, id, value, new Sprite(img, x, y, id));
		this.x = x;
		this.y = y;
	}
	
	public final static int[][] KC_Z_S_ids = {
			{7,4,1},
			{6,3,0},
			{8,5,2}	
	};	
}
