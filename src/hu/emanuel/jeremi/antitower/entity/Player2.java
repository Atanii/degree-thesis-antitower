package hu.emanuel.jeremi.antitower.entity;

import hu.emanuel.jeremi.antitower.entity.item.Item;
import hu.emanuel.jeremi.antitower.entity.item.ItemType;

public class Player2 extends Entity {

	int hp;
	int dp;
	
	public int speed;
	public int rotateSpeed;
	public int FOV;
	public int playerPaneDist;
	public boolean isInside;
	
	public boolean LEFT = false; 
	public boolean RIGHT = false; 
	public boolean UP = false;
	public boolean DOWN = false;
	public boolean STEPLEFT = false;
	public boolean STEPRIGHT = false;
	public boolean INTERACTING = false;
	public boolean SHOOTING = false;
	
	public int angle;
	
	private static final int INVENTORY_SIZE = 5;
	
	private Item inventory[];
	private int actualItemPointer;
	
	private void initPlayer() {
		inventory = new Item[INVENTORY_SIZE];
		actualItemPointer = -1;
		actualItemPointer = 0;
	}
	
	public Player2() {
		initPlayer();
	}
	
	public boolean interact(Interactive toInteractWith) {
		INTERACTING = false;
		if(toInteractWith == null)
			return false;
		else
			return toInteractWith.interactWithPlayer(inventory[actualItemPointer].value);
	}
	
	public boolean addItem(Item item) {
		for(int i = 0; i < INVENTORY_SIZE; i++)
			if(inventory[i] == null) {
				inventory[i] = item;
				return true;
			}
		return false;
				
	}
	
	public boolean addItem(Item...items) {
		for(Item item : items) {
			for(int i = 0; i < INVENTORY_SIZE; i++)
				if(inventory[i] == null) {
					inventory[i] = item;
					break;
				}
				else if( i == INVENTORY_SIZE-1 ) {
					return false;
				}
		}
		return true;
	}
	
	public void removeItem(int id) {
		Item temp[] = new Item[INVENTORY_SIZE];
		
		for(int i = 0, i2 = 0; i < inventory.length; i++) {
			if(inventory[i] != null) {
				if(inventory[i].id == id)
					inventory[i] = null;
				else
					temp[i2++] = inventory[i];
			}
		}
		
		inventory = temp.clone();
	}
	
	public Item getSelectedItem() {		
		return actualItemPointer == -1 ? null : inventory[actualItemPointer];
	}
	
	public void chooseItem(int slot) {
		if(inventory[slot-1] != null)
			actualItemPointer = slot-1;
	}
	
	public void takeDamage(int dmg) {
		System.out.println("You got injured!");
		return;
	}
	
	public void setShooting(boolean to) {
		if(to && (inventory[actualItemPointer].type == ItemType.WEAPON) ) {
			SHOOTING = true;
		} else {
			SHOOTING = false;
		}
	}
	
}
