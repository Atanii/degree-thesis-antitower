package hu.emanuel.jeremi.antitower.entity;

import hu.emanuel.jeremi.antitower.effect.Sound;

public class ToggleDoor extends Entity implements Interactive {
	
	private int key;
	public boolean isClosed;
	public int closed, opened;
	
	public ToggleDoor(int closed, int opened,int x, int y, int key, boolean isClosed) {
		super(x,y,key);
		this.x = x;
		this.y = y;
		this.closed = closed;
		this.opened = opened;
		this.key = key;
		this.isClosed = isClosed;
	}
	
	public void toggle() {
		isClosed = isClosed ? false : true;
		(new Sound("43677__stijn__click11.wav")).play();
		//System.out.println("Toggled!");
	}
	
	public int getActualTexture() {
		return isClosed ? closed : opened;
	}

	@Override
	public boolean interactWithPlayer(int key_card_id) {
		if(this.key == key_card_id) {
			toggle();
			return true;	
		} else {
			return false;
		}		
	}

	@Override
	public int getMapX() {
		return x;
	}

	@Override
	public int getMapY() {
		return y;
	}

}
