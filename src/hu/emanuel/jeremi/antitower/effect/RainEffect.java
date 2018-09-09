package hu.emanuel.jeremi.antitower.effect;

import java.awt.Color;

public class RainEffect {

	public int dropCount;
	public int dy,dx;
	public Color color;
	public int[] drops;
	public int height, width;
	
	public RainEffect(int width, int maxheight) {
		this.width = width;
		this.height = maxheight;
		dropCount = 500;
		dy = 30;
		dx = 0;
		color = Color.BLUE;
		drops = new int[dropCount];
		
		for(int i = 0; i < drops.length; i++)
			drops[i] = Integer.MIN_VALUE;
		
		generateDrops();
	}
	
	public void generateDrops() {
		for(int i = 0; i < drops.length-1; i+=2) {
			if(drops[i] == Integer.MIN_VALUE) {
				drops[i] = (int) Math.floor( (Math.random() * width) );
				drops[i+1] = (int) -Math.floor( (Math.random() * 1500) );
			}
		}
	}
	
	public void fall() {
		for(int i = 0; i < drops.length-1; i += 2) {
	      drops[i] += dx;
	      drops[i+1] += dy;
	      if(drops[i+1] >= this.height) {
	        drops[i] = Integer.MIN_VALUE;
	        drops[i+1] = Integer.MIN_VALUE;
	      }
	    }
	}
	
}
