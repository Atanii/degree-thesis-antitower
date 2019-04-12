package hu.emanuel.jeremi.antitower.entity.item;;

import hu.emanuel.jeremi.antitower.entity.Sprite;
import java.awt.image.BufferedImage;

/**
 * This class represents all the usable and assumable items in the game such as weapons, keycards, shields...etc.
 * 
 * Since all item types use the same attributes and have the same features, there's no inheritance, nor multiple classes, only the Item class and
 * the type of the item is determined by the value of the type (ItemType enum).
 * 
 * The id is simply the id number (or serial number) of the actual item while the "value" is the value used in interactions. For example
 * the value of a weapon is the damage caused by the weapon, the value of a keycard is the code the card has... 
 * 
 * @author Kádár Jeremi Emánuel
 *
 */
public class Item {
	
	private class UndeFinedWeaponType extends Exception {}
	
	public ItemType type;
	
	public int id;
	public int value;
	
	public Sprite sprite;
	public int icon;
	public int overheadImg;
    
    public int x,y;
    
    public final static int[][] KC_Z_S_ids = {
			{7,4,1},
			{6,3,0},
			{8,5,2}	
	};	
	
	public Item(ItemType type, int id, int value, int x, int y, BufferedImage img) {
		this.x = x;
		this.y = y;
        
        this.type = type;
        this.id = id;
		
		try {
			switch(type) {
			case ZAPPER:
				overheadImg = 0; 
				icon = 3;
                this.value = 20;
				this.sprite = new Sprite(img, x, y, id);
				break;
			case KEY_CARD:
				overheadImg = 1; 
				icon = 4;
                this.value = value;
				this.sprite = new Sprite(img, x, y, id);
				break;
			case SHIELD:
				overheadImg = 2; 
				icon = 5;
                this.value = 2000;
				this.sprite = new Sprite(img, x, y, id);
				break;
			default:
				throw new UndeFinedWeaponType();
			}
		} catch (UndeFinedWeaponType e) {
			System.out.println("Undefined weapon type! Could not be created. Zapper instance created instead.");
			this.type = ItemType.ZAPPER;
		}
	}
	
	public Item(ItemType type, int id, int value, Sprite sprite) {
		this.type = type;
		this.id = id;
		this.value = value;
		
		try {
			switch(type) {
			case ZAPPER:
				overheadImg = 0; 
				icon = 3;
				this.sprite = sprite;
				break;
			case KEY_CARD:
				overheadImg = 1; 
				icon = 4;
				this.sprite = sprite;
				break;
			case SHIELD:
				overheadImg = 2; 
				icon = 5;
				this.sprite = sprite;
				break;
			default:
				throw new UndeFinedWeaponType();
			}
		} catch (UndeFinedWeaponType e) {
			System.out.println("Undefined weapon type! Could not be created. Zapper instance created instead.");
			this.type = ItemType.ZAPPER;
		}
	}
    
    public Item(ItemType type, int id, int value) {
		this.type = type;
		this.id = id;
		this.value = value;
		
		try {
			switch(type) {
			case ZAPPER:
				overheadImg = 0; 
				icon = 3;
				break;
			case KEY_CARD:
				overheadImg = 1; 
				icon = 4;
				break;
			case SHIELD:
				overheadImg = 2; 
				icon = 5;
				break;
			default:
				throw new UndeFinedWeaponType();
			}
		} catch (UndeFinedWeaponType e) {
			System.out.println("Undefined weapon type! Could not be created. Zapper instance created instead.");
			this.type = ItemType.ZAPPER;
		}
	}
    
}