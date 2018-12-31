package hu.emanuel.jeremi.antitower.entity;

import hu.emanuel.jeremi.antitower.entity.Sprite.SpriteSequence;
import hu.emanuel.jeremi.antitower.graphic.GetSpriteImage;

public class Enemy extends Entity {
	
    // <editor-fold defaultstate="collapsed" desc="enums, variables, constants">
	public enum EnemyType {
		BZZZZ_TOWER, SCOPE_TOWER, RIFLE_TOWER
	}
	
	public boolean hostile;
	public boolean standing;
	public boolean destroyed;
	public int hp;
	public int dmg;
	public int dp;
	public byte level;
	public int xpcaps[];
	public int speed;
	public int xp;
	public int xpPerShoot;
	
	private byte timerInSeconds;
	private long now;
	private long timeOut;
	
	public SpriteSequence frames;
	public int framePointer;
    // </editor-fold>
	
    /*
	public Enemy(int id, int x, int y, boolean hostile, EnemyType type, int spritesheet[], int sheetWidth) {
		super(x, y, id);
		this.now = 0;
		this.hostile = hostile;
		setConfiguration(type, spritesheet, sheetWidth);
	}
    */
        
    public Enemy(int id, int x, int y, boolean hostile, EnemyType type, GetSpriteImage seqGetter) {
        super(x, y, id);
        this.now = 0;
        this.hostile = hostile;
        this.frames = seqGetter.getEnemySprites(type, x, y, id);
        setConfiguration(type);
	}
        
        private void setConfiguration(EnemyType type) {
		switch(type) {
			case BZZZZ_TOWER: {
				this.hp = 100;
				this.dmg = 20;
				this.timerInSeconds = 1;
				this.level = 0;
				this.xpPerShoot = 20;
				this.xpcaps = new int[] {
					2000, 4000	
				};
				break;
			}
			case SCOPE_TOWER: {
				this.hp = 20;
				this.dmg = 200;
				this.timerInSeconds = 4;
				this.level = 0;
				this.xpPerShoot = 250;
				this.xpcaps = new int[] {
					2000, 4000	
				};
				break;
			}
			case RIFLE_TOWER: {
				this.hp = 150;
				this.dmg = 50;
				this.timerInSeconds = 2;
				this.level = 0;
				this.xpPerShoot = 40;
				this.xpcaps = new int[] {
					2000, 4000	
				};
				break;
			}
			default: {
				this.hp = 100;
				this.dmg = 20;
				this.timerInSeconds = 0;
				this.level = 0;
				this.xpPerShoot = 20;
				this.xpcaps = new int[] {
					2000, 4000	
				};
				break;
			}
		}
	}
	
    /*
	private void setConfiguration(EnemyType type, int[] spritesheet, int sheetWidth) {		
		int temp[] = new int[sheetWidth];
		
		switch(type) {
			case BZZZZ_TOWER: {
				this.hp = 100;
				this.dmg = 20;
				this.timerInSeconds = 0;
				this.level = 0;
				this.xpPerShoot = 20;
				this.xpcaps = new int[] {
					1000, 2000	
				};
				
				for(int i = 0; i < temp.length; i++) {
					temp[i] = spritesheet[i + (0 * sheetWidth)];
				}
				
				break;
			}
			case SCOPE_TOWER: {
				this.hp = 20;
				this.dmg = 200;
				this.timerInSeconds = 4;
				this.level = 0;
				this.xpPerShoot = 500;
				this.xpcaps = new int[] {
					1000, 2000	
				};
				
				for(int i = 0; i < temp.length; i++) {
					temp[i] = spritesheet[i + (1 * sheetWidth)];
				}
				
				break;
			}
			case RIFLE_TOWER: {
				this.hp = 150;
				this.dmg = 50;
				this.timerInSeconds = 2;
				this.level = 0;
				this.xpPerShoot = 40;
				this.xpcaps = new int[] {
					1000, 2000	
				};
				
				for(int i = 0; i < temp.length; i++) {
					temp[i] = spritesheet[i + (2 * sheetWidth)];
				}
				
				break;
			}
			default: {
				this.hp = 100;
				this.dmg = 20;
				this.timerInSeconds = 0;
				this.level = 0;
				this.xpPerShoot = 20;
				this.xpcaps = new int[] {
					1000, 2000	
				};
				
				for(int i = 0; i < temp.length; i++) {
					temp[i] = spritesheet[i + (0 * sheetWidth)];
				}
				
				break;
			}
		}
		
		frames = new SpriteSequence(temp, 0, this.x, this.y, this.id);
	}
    */
	
	public Sprite getFrame() {
		return frames;
	}

	public void takeDamage(int dmg) {
		this.hp -= dmg;
		if(!destroyed && hp <= 0) {
			destroyed = true;
			frames.setActualFrame(frames.getFramePointer() + 1);
		}
	}
	
	public void gainXP() {
		xp += xpPerShoot;
		if(level < xpcaps.length && level < 3 && xp >= xpcaps[level]) {
			level++;
			frames.setActualFrame(frames.getFramePointer() + 2);
			//System.out.println("[Enemy] New level: "+level);
			syncAttributesWithNewLevel();
		}
	}
	
	private void syncAttributesWithNewLevel() {
		dmg += 10;
		dp += 5;
	}
	
	public boolean shootIfItsTime() {
		if (now == 0) {
			now = System.currentTimeMillis();
			timeOut = now + timerInSeconds * 1000;
			return true;
		} 
		else if ( (now = System.currentTimeMillis()) >= timeOut ) {
			timeOut = now + timerInSeconds * 1000;
			return true;
		} else {
			return false;
		}
	}
    
    public SpriteSequence getSpriteSequence() {
        return this.frames;
    }

}
