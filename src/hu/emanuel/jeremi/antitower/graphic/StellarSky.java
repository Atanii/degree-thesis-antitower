/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.emanuel.jeremi.antitower.graphic;

import java.util.Random;

/**
 * Stellar, scrollable night sky.
 * @author Jeremi
 */
public class StellarSky {
    
    int[] skyImage;
    int planeWidth, planeHeight;
    final int w, h, seed;
    final int num = 500;
    final Random rand;
    int n;
    
    public StellarSky(int planeWidth, int planeHeight, int seed) {
        this.planeWidth = planeWidth;
        this.planeHeight = planeHeight;
        this.seed = seed;        
        w = planeWidth * 6;
        h = (planeHeight >> 1);
        rand = new Random(seed);
        skyImage = new int[w * h];        
        generateNightSky();
    }
    
    private void generateNightSky() {
        skyImage = new int[w * h];
        
        for(int y = 0; y < h; y++) {
            for(int x = 0; x < w; x++) {
                n = rand.nextInt(num) + 1;
                skyImage[y * w + x] = n > 10 ? 0xff000000 : 0xffffffff;
            }
        }
    }
    
    public int getSkyPixel(final int x, final int y, final int offsetX) {
        return skyImage[y * w + ((offsetX + x) % w)];
    }
    
    /*
    private void generateNightSky() {
        final int w = 2160;
        final int h = (planeHeight >> 1);
        final int seed = 500;
        final int num = 500;
        
        skyImage = new int[w * h]; // 2160 x 240
        
        final Random rand = new Random(seed);
        int n;
                
        final int offsetX = player.angle;
        
        for(int y = 0; y < h; y++) {
            for(int x = 0; x < w; x++) {
                n = rand.nextInt(num) + 1;
                skyImage[y * w + x] = n > 10 ? 0xff000000 : 0xffffffff;
            }
        }
        
        for(int y = 0; y < h; y++) {
            for(int x = 0; x < planeWidth; x++) {
                output[y * planeWidth + x] = skyImage[y * w + ((offsetX + x) % w)];
            }
        }
    }
    */
    
}
