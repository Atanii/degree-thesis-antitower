package hu.emanuel.jeremi.antitower.graphic;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

public final class ImageAndPostProcessHelper {

	/////////////////////////////// POST PROCESS /////////////////////////////////////////////
	/**
	 * A távolságnak megfelelõen "ködösíti" az adott színt.
	 * @param color
	 * @param distance
	 * @return transzformált szín
	 */
	public static final int addFogEffect(int color, final float distance) {
		/*
		return color;
		*/
		
		// Java:
		// pixel = 8 + 8 + 8 + 8 bit, int
		//		   A   R   G   B
		
		// Getting color components one by one and fading them:
		
		int R = ((color>>16) & 0xff)-(((int)distance & 2047)>>1);// % 256);// & 0xff;	// increment red component
		int G = ((color>>8) & 0xff)-(((int)distance & 2047)>>1);// % 256);// & 0xff;	// increment green component
		int B = (color & 0xff)-(((int)distance & 2047)>>1);// % 256);// & 0xff;	// increment blue component
		
		R = R < 0 ? 0 : R;
		G = G < 0 ? 0 : G;
		B = B < 0 ? 0 : B;
		
		// Returning "fogged" color:
		return (255<<24)|(R<<16)|(G<<8)|B;
		
	}
	
	/**
	 * A távolságnak megfelelõen "ködösíti" az adott színt.
	 * @param colors
	 * @param distance
	 * @return transzformált színek tömbje
	 */
	public static final int[] addFogEffect(int colors[], final float distance) {
		// Java:
		// pixel = 8 + 8 + 8 + 8 bit, int
		//		   A   R   G   B
		
		int R,G,B;
		
		for(int i = 0; i < colors.length; i++) {
			R = ((colors[i]>>16) & 0xff)-(((int)distance & 2047)>>1);	// & 0xff;	// increment red component
			G = ((colors[i]>>8) & 0xff)-(((int)distance & 2047)>>1);	// & 0xff;	// increment green component
			B = (colors[i] & 0xff)-(((int)distance & 2047)>>1);		// & 0xff;	// increment blue component
			R = R < 0 ? 0 : R;
			G = G < 0 ? 0 : G;
			B = B < 0 ? 0 : B;
			colors[i] = (255<<24)|(R<<16)|(G<<8)|B;
		}
		
		// Returning "fogged" colors:
		return colors;
	}
	
	
	/**
	 * A távolságnak megfelelõen "ködösíti" az adott színt.
	 * @param color
	 * @param distance
	 * @return transzformált szín
	 */
	public static final int addFogEffect(int color, final int distance) {
		
		// Java:
		// pixel = 8 + 8 + 8 + 8 bit, int
		//		   A   R   G   B
		
		// Getting color components one by one:		
		
		int R = ((color>>16) & 0xff)-(((int)distance % 2000));	// increment red component
		int G = ((color>>8) & 0xff)-(((int)distance % 2000));	// increment green component
		int B = (color & 0xff)-(((int)distance % 2000));		// increment blue component
		
		R = R < 0 ? 0 : R;
		G = G < 0 ? 0 : G;
		B = B < 0 ? 0 : B;
		
		// Returning "fogged" color:
		return (255<<24)|(R<<16)|(G<<8)|B;
	}
	//////////////////////////////////////////////////////////////////////////////////////////
	
	/////////////////////////////// IMAGE MAINPULATION ///////////////////////////////////////
	/**
	 * Nearest-neighbor-image-scaling function.
	 * Source: http://tech-algorithm.com/articles/nearest-neighbor-image-scaling/
	 * @param pixels -- pixels of the original image
	 * @param w1	 -- original width
	 * @param h1 	 -- original height
	 * @param w2	 -- new width
	 * @param h2	 -- new height
	 * @return pixels of scaled image
	 */
	public static final int[] resizePixels(int[] pixels,int w1,int h1,int w2,int h2) {
	    int[] temp = new int[w2*h2] ;
	    // EDIT: added +1 to account for an early rounding problem
	    int x_ratio = (int)((w1<<16)/w2) +1;
	    int y_ratio = (int)((h1<<16)/h2) +1;
	    //int x_ratio = (int)((w1<<16)/w2) ;
	    //int y_ratio = (int)((h1<<16)/h2) ;
	    int x2, y2 ;
	    for (int i=0;i<h2;i++) {
	        for (int j=0;j<w2;j++) {
	            x2 = ((j*x_ratio)>>16) ;
	            y2 = ((i*y_ratio)>>16) ;
	            temp[(i*w2)+j] = pixels[(y2*w1)+x2] ;
	        }                
	    }                
	    return temp ;
	}
	
	// https://stackoverflow.com/questions/4216123/how-to-scale-a-bufferedimage
	
	public static BufferedImage scaleNearest(BufferedImage before, double scale) {
	    final int interpolation = AffineTransformOp.TYPE_NEAREST_NEIGHBOR;
	    //return scale(before, scale, interpolation);
	    return scale(before, scale, interpolation);
	}

	private static BufferedImage scale(final BufferedImage before, final double scale, final int type) {
	    int w = before.getWidth();
	    int h = before.getHeight();
	    int w2 = (int) (w * scale);
	    int h2 = (int) (h * scale);
	    BufferedImage after = new BufferedImage(w2, h2, before.getType());
	    AffineTransform scaleInstance = AffineTransform.getScaleInstance(scale, scale);
	    AffineTransformOp scaleOp = new AffineTransformOp(scaleInstance, type);
	    scaleOp.filter(before, after);
	    return after;
	}
	
	public static BufferedImage scaleNearest(BufferedImage before, final int w2, final int h2) {
	    final int interpolation = AffineTransformOp.TYPE_NEAREST_NEIGHBOR;
	    //return scale(before, scale, interpolation);
	    return scale(before, w2, h2, interpolation);
	}
	
	private static BufferedImage scale(final BufferedImage before, final int w2, final int h2, final int type) {
	    int w = before.getWidth();
	    int h = before.getHeight();
	    BufferedImage after = new BufferedImage(w2, h2, before.getType());
	    AffineTransform scaleInstance = AffineTransform.getScaleInstance(w2/w, h2/h);
	    AffineTransformOp scaleOp = new AffineTransformOp(scaleInstance, type);
	    scaleOp.filter(before, after);
	    return after;
	}
	//////////////////////////////////////////////////////////////////////////////////////////

}
