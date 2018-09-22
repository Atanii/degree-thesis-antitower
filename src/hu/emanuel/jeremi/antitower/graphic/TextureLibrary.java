package hu.emanuel.jeremi.antitower.graphic;

import static hu.emanuel.jeremi.antitower.common.Tile64.*;
import hu.emanuel.jeremi.antitower.entity.Enemy.EnemyType;
import hu.emanuel.jeremi.antitower.entity.Sprite.SpriteSequence;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

public class TextureLibrary implements GetEnemySpriteSequence {
	/////////////////////////////// VARIABLES, CONSTS... /////////////////////////////////////
	public static final boolean DEBUG = true;
	public static final int startTextureId = START_ID;
	public int id = 0;
        
        private static final int SPRITES_ROW_LENGTH = 6;
        private static final int ENEMY_SPSEQ_START_FRAME = 0;
	
	public class Sheet {
		public BufferedImage s;
		public int w, h, l;
		
		public Sheet (BufferedImage s, int w, int h) {
			this.s = s;
			this.w = w;
			this.h = h;
			this.l = w * h;
		}
	}
	
	public Sheet sprites;
	public Sheet items;
	public Sheet[] texturePacks;
	public Sheet[] tiles;
	
	public BufferedImage img[][];
	
	public int length;
	//////////////////////////////////////////////////////////////////////////////////////////
	
	/////////////////////////////// CONSTRUCTORS /////////////////////////////////////////////
	public TextureLibrary(String spritesheet_filename, String itemsheet_filename, String...texturepacks) {
		sprites = loadSpriteSheet(spritesheet_filename);
		items = loadSpriteSheet(itemsheet_filename);
		texturePacks = loadTexturePacks(texturepacks);
		
		tiles = new Sheet[2 + texturePacks.length];
		tiles[0] = items;
		tiles[1] = sprites;
		for(int i = 0; i < texturePacks.length; i++) {
			tiles[i+2] = texturePacks[i];
		}
		
		// texture packs;
		img = new BufferedImage[2 + texturePacks.length][];
		
		// items at index of 0
		img[0] = new BufferedImage[items.l];
		for (int i = 0; i < items.l; i++) {
			img[0][i] = getItem(i);
		}
		
		// sprites at intex of 1
		img[1] = new BufferedImage[sprites.l];
		for (int i = 0; i < sprites.l; i++) {
			img[1][i] = getSprite(i);
		}
		
		// texture packs at index of rest
		for (int i = 2; i < tiles.length; i++) {
			img[i] = new BufferedImage[tiles[i].l];
			for (int x = 0; x < tiles[i].l; x++) {
				//System.out.println("Pack size: " + tiles[2 + i].l + "| id: " + x);
				img[i][x] = getTexture2(i, x);
			}
		}
	}
	//////////////////////////////////////////////////////////////////////////////////////////

	/////////////////////////////// SHEET PROCESSING /////////////////////////////////////////
	private final Sheet loadItemSheet(String filename) {
		Sheet sheet = null;
		
		try {
			// getting texture atlas
			URL url = getClass().getResource("/res/" + filename);
			sheet = new Sheet(ImageIO.read(url).getSubimage(0, 2 * SIZE, ImageIO.read(url).getWidth(), SIZE),
					ImageIO.read(url).getWidth() >> SIZE_LOG, 1);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return sheet;
	}
	
	private final Sheet loadSpriteSheet(String filename) {
		Sheet sheet = null;
		
		try {
			// getting texture atlas
			URL url = getClass().getResource("/res/" + filename);
			sheet = new Sheet(ImageIO.read(url).getSubimage(0, 0, SIZE, ImageIO.read(url).getHeight()), 
					1, ImageIO.read(url).getHeight() >> SIZE_LOG);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return sheet;
	}
	
	private final Sheet[] loadTexturePacks(String...filenames) {
		Sheet[] sheets = new Sheet[filenames.length];
		
		try {
			for (int i = 0; i < filenames.length; i++) {
				// getting texture atlas
				URL url = getClass().getResource("/res/" + filenames[i]);
				sheets[i] = new Sheet(ImageIO.read(url), ImageIO.read(url).getWidth() >> SIZE_LOG, ImageIO.read(url).getHeight() >> SIZE_LOG);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return sheets;
	}
	//////////////////////////////////////////////////////////////////////////////////////////
	
	/////////////////////////////// IMAGE ////////////////////////////////////////////////////
	public BufferedImage getTexture2(int pack, int id) {
		//System.out.println("Pack: " + pack + "\nId: " + id);
		
		int w = this.tiles[pack].s.getWidth() >> SIZE_LOG;
		
		int col = ( id % w ) << SIZE_LOG;
		int row = (int) Math.floor(id / w) << SIZE_LOG;
		
		length = col * row;
		
		System.out.println("\nw: " + w + "\nid: " + id + "\ncol: " + col + "\nrow: " + row);
		
		return this.tiles[pack].s.getSubimage(col, row, SIZE, SIZE);
	}
	
	public BufferedImage getTexture(int pack, int id) {		
		return img[pack][id];		
	}
	
	public BufferedImage getItem(int id) {
		int w = this.items.s.getWidth() >> SIZE_LOG;
		
		int col = ( id % w ) << SIZE_LOG;
		int row = (int) Math.floor(id / w) << SIZE_LOG;
		
		return this.items.s.getSubimage(col, row, SIZE, SIZE);
	}
	
	public BufferedImage getSprite(int id) {
		int w = this.items.s.getWidth() >> SIZE_LOG;
		
		int col = ( id % w ) << SIZE_LOG;
		int row = (int) Math.floor(id / w) << SIZE_LOG;
		
		return this.sprites.s.getSubimage(col, row, SIZE, SIZE);
	}
	
	/**
	 * Load an image from the resources folder (res) depending on the filename and
	 * wrap it in a Texture object with the given id.
	 * 
	 * @param filename
	 * @param id
	 * @return the BufferedImage wrapped in a Texture object
	 */
	public BufferedImage loadAndGetTextureFromImageFile(String filename, int id) {
		try {
			// getting texture atlas
			URL url = getClass().getResource("/res/" + filename);
			return ImageIO.read(url);
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
	public BufferedImage loadAndGetTextureFromImageFile(String filename, int id, int x, int y, int w, int h) {
		try {
			// getting texture atlas
			URL url = getClass().getResource("/res/" + filename);
			return ImageIO.read
					(getClass().getResource("/res/" + filename)).getSubimage(x, y, w, h);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}
        /*
        public GetEnemySpriteSequence getEnemySeqCallback = (EnemyType type, int x, int y, int id) -> {
            int temp[] = new int[SPRITES_ROW_LENGTH];
            
            switch(type) {
                case BZZZZ_TOWER: {				
                        for(int i = 0; i < temp.length; i++) {
                                temp[i] = i + (0 * SPRITES_ROW_LENGTH);
                        }				
                        break;
                }
                case SCOPE_TOWER: {
                        for(int i = 0; i < temp.length; i++) {
                                temp[i] = i + (1 * SPRITES_ROW_LENGTH);
                        }				
                        break;
                }
                case RIFLE_TOWER: {
                        for(int i = 0; i < temp.length; i++) {
                                temp[i] = i + (2 * SPRITES_ROW_LENGTH);
                        }				
                        break;
                }
                default: {
                        for(int i = 0; i < temp.length; i++) {
                                temp[i] = i + (0 * SPRITES_ROW_LENGTH);
                        }				
                        break;
                }
            }
            
            return new SpriteSequence(temp, ENEMY_SPSEQ_START_FRAME, x, y, id);
        };
        */
        @Override
        public SpriteSequence getEnemySprites(EnemyType type, int x, int y, int id) {
            int temp[] = new int[SPRITES_ROW_LENGTH];
            
            switch(type) {
                case BZZZZ_TOWER: {				
                        for(int i = 0; i < temp.length; i++) {
                                temp[i] = i + (0 * SPRITES_ROW_LENGTH);
                        }				
                        break;
                }
                case SCOPE_TOWER: {
                        for(int i = 0; i < temp.length; i++) {
                                temp[i] = i + (1 * SPRITES_ROW_LENGTH);
                        }				
                        break;
                }
                case RIFLE_TOWER: {
                        for(int i = 0; i < temp.length; i++) {
                                temp[i] = i + (2 * SPRITES_ROW_LENGTH);
                        }				
                        break;
                }
                default: {
                        for(int i = 0; i < temp.length; i++) {
                                temp[i] = i + (0 * SPRITES_ROW_LENGTH);
                        }				
                        break;
                }
            }
            
            return new SpriteSequence(temp, ENEMY_SPSEQ_START_FRAME, x, y, id);
        }
	//////////////////////////////////////////////////////////////////////////////////////////
	
	/////////////////////////////// MISC. ////////////////////////////////////////////////////
	// TODO: Need here anything?
	//////////////////////////////////////////////////////////////////////////////////////////	
}
