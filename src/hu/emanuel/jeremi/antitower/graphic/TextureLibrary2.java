package hu.emanuel.jeremi.antitower.graphic;

import static hu.emanuel.jeremi.antitower.common.Tile64.*;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

public class TextureLibrary2 {

	/////////////////////////////// TEXTURE CLASS ////////////////////////////////////////////
	public class Texture {
		public int id;
		public BufferedImage img;
		Texture(int id, BufferedImage img) {
			this.id = id;
			this.img = img;
		}
	}
	//////////////////////////////////////////////////////////////////////////////////////////
	
	/////////////////////////////// VARIABLES, CONSTS... /////////////////////////////////////
	public static final int startTextureId = START_ID;
	public int id = 0;
	
	public Texture textures[];
	public Texture sprites[];
	public Texture items_hud[];
	//////////////////////////////////////////////////////////////////////////////////////////
	
	/////////////////////////////// CONSTRUCTORS /////////////////////////////////////////////
	public TextureLibrary2(String texture_atlas_filename, String spritesheet_filename, String itemsheet_filename, boolean print) {
		textures = processImageSheet(texture_atlas_filename);
		sprites = processImageSheet(spritesheet_filename);
		items_hud = processImageSheet(itemsheet_filename);
		if(print)
			printArrays();
	}
	
	public TextureLibrary2(BufferedImage textureatlas, BufferedImage spritesheet, BufferedImage itemsheet, boolean print) {
		textures = processImageSheet(textureatlas);
		sprites = processImageSheet(spritesheet);
		items_hud = processImageSheet(itemsheet);
		if(print)
			printArrays();
	}
	//////////////////////////////////////////////////////////////////////////////////////////

	/////////////////////////////// SHEET PROCESSING /////////////////////////////////////////
	private final Texture[] processImageSheet(BufferedImage sheet) {
		id = startTextureId;
		
		Texture[] container = null;
		
		try {
			// variables for image width, height and amount of textures on the atlas
			int w,h,amount;
			
			// getting texture atlas
			BufferedImage temp = sheet;
			
			// getting width and height
			w = temp.getWidth()/SIZE;
			h = temp.getHeight()/SIZE;
			
			// amount of SIZE*SIZE textures on atlas
			amount = w*h;
			
			// preparing place for the textures
			container = new Texture[amount];
			
			// getting textures one by one
			for(int y = 0; y < h; y++) {
				for(int x = 0; x < w; x++) {
					container[y*w+x] = new Texture(id++, temp.getSubimage(x*SIZE, y*SIZE, SIZE, SIZE) ); 
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return container;
	}
	
	private final Texture[] processImageSheet(String filename) {
		id = startTextureId;
		
		Texture[] container = null;
		
		try {
			// variables for image width, height and amount of textures on the atlas
			int w,h,amount;
			
			// getting texture atlas
			URL url = getClass().getResource("/res/"+filename);
			BufferedImage temp = ImageIO.read(url);
			
			// getting width and height
			w = temp.getWidth()/SIZE;
			h = temp.getHeight()/SIZE;
			
			// amount of SIZE*SIZE textures on atlas
			amount = w*h;
			
			// preparing place for the textures
			container = new Texture[amount];
			
			// getting textures one by one
			for(int y = 0; y < h; y++) {
				for(int x = 0; x < w; x++) {
					container[y*w+x] = new Texture(id++, temp.getSubimage(x*SIZE, y*SIZE, SIZE, SIZE) ); 
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return container;
	}
	//////////////////////////////////////////////////////////////////////////////////////////
	
	/////////////////////////////// IMAGE ////////////////////////////////////////////////////
	/**
	 * Load an image from the resources folder (res) depending on the filename and
	 * wrap it in a Texture object with the given id.
	 * 
	 * @param filename
	 * @param id
	 * @return the BufferedImage wrapped in a Texture object
	 */
	public Texture loadAndGetTextureFromImageFile(String filename, int id) {
		try {
			return new Texture(
					id,
					ImageIO.read(
								getClass().getResource("/res/"+filename)
							)
			); 
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Load an image from the resources folder (res) depending on the filename and
	 * wrap it in a Texture object with the given id.
	 * 
	 * @param filename
	 * @param id
	 * @param x
	 * @param y
	 * @param w
	 * @param h
	 * @return
	 */
	public Texture loadAndGetTextureFromImageFile(String filename, int id, int x, int y, int w, int h) {
		try {
			return new Texture(
					id,
					ImageIO.read
						(getClass().getResource("/res/"+filename)).getSubimage(x,y,w,h)
							
			); 
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	//////////////////////////////////////////////////////////////////////////////////////////
	
	/////////////////////////////// MISC. ////////////////////////////////////////////////////
	public BufferedImage getTexImg(int id) {
		return textures[id].img;
	}
	
	public BufferedImage getSpriteImg(int id) {
		return sprites[id].img;
	}
	
	public BufferedImage getItemHudImg(int id) {
		return items_hud[id].img;
	}
	
	private void printArrays() {
		for(int i = 0; i < textures.length; i++) {
			System.out.println("textures["+i+"] => id = "+textures[i].id);
		}
		for(int i = 0; i < sprites.length; i++) {
			System.out.println("sprites["+i+"] => id = "+sprites[i].id);
		}
	}
	
	@Override
	public String toString() {
		return null;
	}
	//////////////////////////////////////////////////////////////////////////////////////////	
}
