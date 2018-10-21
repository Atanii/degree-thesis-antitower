package hu.emanuel.jeremi.antitower.entity;

/**
 * This class represents the model part of some entities like decorative images or npc in the game.
 * @author Jeremi Emánuel Kádár
 */
public class Sprite {
    
    public int texture;
	public int id;
	public int x,y;
	public float distanceFromPlayer;
	
	public static class SpriteSequence extends Sprite {

		private int frames[];
		private int framePointer;
		
		public SpriteSequence(int tex[], int actualFrame, int x, int y, int id) {
			super(tex[actualFrame], x, y, id);
			frames = tex;
			setFramePointer(actualFrame);
		}
		
		public void setActualFrame(int framePointer) {
			if(framePointer < frames.length) {
				setFramePointer(framePointer);
				texture = frames[framePointer];
			}	
		}

		public int getFramePointer() {
			return framePointer;
		}

		public void setFramePointer(int framePointer) {
			if(framePointer < frames.length)
				this.framePointer = framePointer;
		}
		
	}
	
	public Sprite(int tex, int x, int y, int id) {
		this.texture = tex;
		this.id = id;
		this.x = x;
		this.y = y;
	}
	
}
