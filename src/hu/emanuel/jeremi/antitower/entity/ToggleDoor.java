package hu.emanuel.jeremi.antitower.entity;

import hu.emanuel.jeremi.antitower.effect.Sound;

public class ToggleDoor implements Interactive {
	
    public int x, y;
	private final int key;
	public boolean isClosed;
	public int closed, opened;
	
	public ToggleDoor(int closed, int opened, int x, int y, int key, boolean isClosed) {
		this.x = x;
		this.y = y;
		this.x = x;
		this.y = y;
		this.closed = closed;
		this.opened = opened;
		this.key = key;
		this.isClosed = isClosed;
	}
	
	public void toggle() {
		isClosed = !isClosed;
		(new Sound("sound/door.wav")).play();
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
