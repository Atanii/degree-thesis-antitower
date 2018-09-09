package hu.emanuel.jeremi.antitower.world;

import java.awt.Graphics;
import java.awt.image.BufferedImage;


/**
 * This class represents the skybox in the game.
 * 
 * @author Emámunel Jeremi Kádár
 *
 */
public class Skybox {

	int x,y;
	int w,h;
	int startX;
	int playerFOV;
	BufferedImage skybox_image;
	
	/**
	 * Only constructor of the Skybox class. The the player's Field of View (FOV) is required to
	 * determine the visible part of the skybox image.
	 * @param playerFOV
	 */
	public Skybox(int playerFOV, BufferedImage skybox_image) {
		this.playerFOV = playerFOV;
		this.x = 0;
		this.y = 0;
		startX = 0;
		this.skybox_image = skybox_image;
		w = skybox_image.getWidth();
		h = skybox_image.getHeight();
	}
	
	/**
	 * Returns the part of the skybox which is in sync with the player's rotation.
	 * @param w
	 * @param h
	 * @return actual part of the skybox
	 */
	public BufferedImage getImage(int w, int h) {
		if(startX > this.w - playerFOV) {
			return
					concatTwoSubimage
					(
							skybox_image.getSubimage(startX, 0, Math.abs(this.w - startX), h),
							skybox_image.getSubimage(0, 0, Math.abs(this.w - startX - w), h)
					);
		}
		else if(startX < 0) {
			return
					concatTwoSubimage
					(
							skybox_image.getSubimage(this.w + startX, 0, Math.abs(this.w - this.w + startX), h),
							skybox_image.getSubimage(0, 0, Math.abs(w - (this.w - this.w + startX)) - 1, h)
					);
		}
		return skybox_image.getSubimage(startX, 0, w, h);
	}
	
	/**
	 * Rotate the skybox in sync with the player's rotation.
	 * @param right
	 * @param speed
	 */
	public void rotate(boolean right, int speed) {
		if(right) {
			if(  (startX += speed) > w  ) {
				startX = 0;
			}
		} 
		else {
			if( (startX -= speed) <= -playerFOV ) {
				startX = w - playerFOV;
			}
		}
	}
	
	/**
	 * Concats two subimage of the skybox.
	 * @param leftPart
	 * @param rightPart
	 * @return concated (new) image
	 */
	public BufferedImage concatTwoSubimage(BufferedImage leftPart, BufferedImage rightPart) {
		BufferedImage temp = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		Graphics g = temp.createGraphics();
		g.drawImage(leftPart, 0, 0, null);
		g.drawImage(rightPart, leftPart.getWidth(), 0, null);
		g.dispose();
		return temp;
	}

}
