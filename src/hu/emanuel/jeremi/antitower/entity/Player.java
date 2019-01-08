package hu.emanuel.jeremi.antitower.entity;

import java.awt.event.KeyEvent;

import hu.emanuel.jeremi.antitower.entity.item.Item;
import hu.emanuel.jeremi.antitower.entity.item.ItemType;
import hu.emanuel.jeremi.antitower.physics.GamePhysicsHelper;

import static hu.emanuel.jeremi.antitower.common.Tile64.SIZE_LOG;
import hu.emanuel.jeremi.antitower.effect.Sound;
import java.time.LocalDateTime;
import java.util.Calendar;

public class Player extends Entity {

	int hp;
	int dp;
	
	public int speed;
	public int rotateSpeed;
	public int FOV;
	public int playerPaneDist;
	public boolean isInside;
    public int lastStepTime = 0;
	
	public boolean LEFT = false; 
	public boolean RIGHT = false; 
	public boolean UP = false;
	public boolean DOWN = false;
	public boolean STEPLEFT = false;
	public boolean STEPRIGHT = false;
	public boolean INTERACTING = false;
	public boolean SHOOTING = false;
	public boolean DEFENSE_MODE = false;
	
	public int angle;
	
	private static final int INVENTORY_SIZE = 5;
	
	private Item inventory[];
	private int actualItemPointer;
	
	private void initPlayer() {
		hp = 100;
		dp = 20;
		
		LEFT = false;
		RIGHT = false;
		UP = false;
		DOWN = false;
		STEPLEFT = false;
		STEPRIGHT = false;
		INTERACTING = false;
		SHOOTING = false;
		
		inventory = new Item[INVENTORY_SIZE];
		actualItemPointer = -1;
		actualItemPointer = 0;
	}
    
    public void clearIntentory() {
        inventory = new Item[INVENTORY_SIZE];
		actualItemPointer = -1;
		actualItemPointer = 0;
    }
	
	public Player() {	
		initPlayer();
	}
	
	public Player(int x, int y) {
		this.x = x;
		this.y = y;
		
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
				else if( i == INVENTORY_SIZE - 1 ) {
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
		if( inventory[slot - 1] != null ) {
			actualItemPointer = slot - 1;
		}
	}
	
	public void takeDamage(int dmg) {
		if( inventory[0] != null && inventory[actualItemPointer].type == ItemType.SHIELD && 
			inventory[actualItemPointer].value > 0 ) {
			this.inventory[actualItemPointer].value -= (dmg-dp);
			if( this.inventory[actualItemPointer].value <= 0 ) {
				this.hp += this.inventory[actualItemPointer].value;
				removeItem( this.inventory[actualItemPointer].id );
				chooseItem( actualItemPointer - 1 );
				DEFENSE_MODE = false;
				return;
			}
		}
		else if( hp >= 0 ) {
			this.hp -= (dmg - dp) >= 0 ? (dmg - dp) : 0;
			return;
		}			
	}
	
	public void setShooting(boolean to) {
		if(to && (inventory[actualItemPointer].type == ItemType.ZAPPER) ) {
			SHOOTING = true;
		} else {
			SHOOTING = false;
		}
	}
	
	public int getHp() {
		return hp;
	}
	
	public int getDp() {
		return dp;
	}
	
	public int getActualItemPointer() {
		return actualItemPointer;
	}
	
	public void keyPressed(KeyEvent e) {
		int code = e.getKeyCode();
		
		if(code == KeyEvent.VK_LEFT)		LEFT = true;
		if(code == KeyEvent.VK_RIGHT)		RIGHT = true;
		if(code == KeyEvent.VK_DOWN 
		|| code == KeyEvent.VK_S)			DOWN = true;
		if(code == KeyEvent.VK_UP 
		|| code == KeyEvent.VK_W)			UP = true;
		
		if(code == KeyEvent.VK_A)			STEPLEFT = true;
		if(code == KeyEvent.VK_D)			STEPRIGHT = true;
		
		if(code == KeyEvent.VK_SPACE)		setShooting(true);
		
		if(code == KeyEvent.VK_1)			chooseItem(1);
		if(code == KeyEvent.VK_2)			chooseItem(2);
		if(code == KeyEvent.VK_3)			chooseItem(3);
		if(code == KeyEvent.VK_4)			chooseItem(4);
		if(code == KeyEvent.VK_5)			chooseItem(5);
		
		if(code == KeyEvent.VK_F)			INTERACTING = true;
	}
	
	public void keyReleased(KeyEvent e) {
		int code = e.getKeyCode();
		
		if(code == KeyEvent.VK_LEFT)		LEFT = false;			
		if(code == KeyEvent.VK_RIGHT)		RIGHT = false;
		if(code == KeyEvent.VK_DOWN 
		|| code == KeyEvent.VK_S)			DOWN = false;
		if(code == KeyEvent.VK_UP
		|| code == KeyEvent.VK_W)			UP = false;
		if(code == KeyEvent.VK_A)			STEPLEFT = false;
		if(code == KeyEvent.VK_D)			STEPRIGHT = false;
		if(code == KeyEvent.VK_SPACE)		setShooting(false);
	}
    
    private final void playStepSound() {
        if(lastStepTime != LocalDateTime.now().getSecond()) {
            (new Sound("sound/step.wav")).play();
            lastStepTime = LocalDateTime.now().getSecond();
        }
    }
	
	public void update(PlayerWorldConnector pwc, long delta) {
		int speed =  (int) (this.speed * delta);
		
		float dx = pwc.getCosTable()[angle]*speed;
		float dy = pwc.getSinTable()[angle]*speed;
		
		if(UP) {
			if( pwc.isCollision(x, y, dx, dy) ) {
				x += (int) dx;
				y += (int) dy;
                playStepSound();
			}		
			if( pwc.isOutside((x >> SIZE_LOG), (y >> SIZE_LOG)) ) {
				isInside = false;
			} else {
				isInside = true;
			}
		}
		if(DOWN) {
			if( pwc.isCollision(x, y, -dx, -dy) ) {
				x -= (int) dx;
				y -= (int) dy;
                playStepSound();
			}			
			if( pwc.isOutside((x >> SIZE_LOG), (y >> SIZE_LOG)) ) {
				isInside = false;
			} else {
				isInside = true;
			}
		}
		if(LEFT) {
			pwc.synchSkyboxWithRotation(true, rotateSpeed);
			if( (angle -= rotateSpeed) < pwc.g().ANGLE0 ) {
				angle += pwc.g().ANGLE360;
			}
		}
		if(RIGHT) {
			pwc.synchSkyboxWithRotation(false, rotateSpeed);
			if( (angle += rotateSpeed) >= pwc.g().ANGLE360 ) {
				angle -= pwc.g().ANGLE360;
			}
		}
		if(STEPLEFT) {
			int temp = angle-pwc.g().ANGLE90;
			if(temp<0) temp += pwc.g().ANGLE360;
			
			dy = pwc.getSinTable()[temp]*speed;
			dx = pwc.getCosTable()[temp]*speed;
			
			if( pwc.isCollision( x, y, dx, dy ) ) {
				y += (int) dy;
				x += (int) dx;
                playStepSound();
			}
			if( pwc.isOutside((x >> SIZE_LOG),(y >> SIZE_LOG)) ) {
				isInside = false;
			} else {
				isInside = true;
			}
		}
		if(STEPRIGHT) {
			int temp = angle + pwc.g().ANGLE90;
			if(temp>pwc.g().ANGLE360) temp -= pwc.g().ANGLE360;
			
			dy = pwc.getSinTable()[temp] * speed;
			dx = pwc.getCosTable()[temp] * speed;
			
			if( pwc.isCollision(x, y, dx, dy) ) {
				y += (int) dy;
				x += (int) dx;
                playStepSound();
			}
			if( pwc.isOutside((x >> SIZE_LOG),(y >> SIZE_LOG)) ) {
				isInside = false;
			} else {
				isInside = true;
			}
		}
		
		if(INTERACTING) {
			pwc.makenteractWithClosestInteractive();
		}
		
		if(SHOOTING) {
			pwc.handleShoot(x, y, angle);
            (new Sound("sound/my_laser.wav")).play();
		}
		
		pwc.updateRendererPlayerReference(x, y, angle, FOV, playerPaneDist);
		pwc.updateDistBetweenndSprites();
		pwc.checkMessagePoint();
		pwc.checkAssumableCollision();
		
		//if( UP || DOWN )
			//System.out.println("X: " + x + "\nY: " + y + "\nAngle: " + angle + "\n======================");
	}
}
