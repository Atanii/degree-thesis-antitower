/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.emanuel.jeremi.antitower.effect;

import java.awt.Color;
import java.awt.Graphics;
import java.util.Random;

/**
 * Class representing the snow weather effect.
 *
 * @see Weather
 * @author Jeremi
 */
public class Snow extends Weather {

    public Snow(final int screenW, final int screenH) {
        super(screenW, screenH);
        config();
    }

    /**
     * @see Weather#config()
     */
    @Override
    public void config() {
        amount = 200;
        particleSize = 10;
        seed = 10;

        xcoo = new int[amount];
        ycoo = new int[amount];
        xcoo2 = new int[amount];
        ycoo2 = new int[amount];
        xcoo3 = new int[amount];
        ycoo3 = new int[amount];
        iterator = 0;
        speed = 5;
    }

    /**
     * @see Weather#generate()
     */
    @Override
    public final void generate() {
        Random rand = new Random(seed);

        // First layer:
        for (int i = 0; i < amount; i++) {
            xcoo[i] = rand.nextInt(screenW - 1);
            ycoo[i] = rand.nextInt(screenH - 1);
        }

        // Second layer:
        for (int i = 0; i < amount; i++) {
            xcoo2[i] = rand.nextInt(screenW - 1);
            ycoo2[i] = rand.nextInt(screenH - 1);
        }

        // Third layer:
        for (int i = 0; i < amount; i++) {
            xcoo3[i] = rand.nextInt(screenW - 1);
            ycoo3[i] = rand.nextInt(screenH - 1);
        }
    }

    /**
     * @see Weather#update(long)
     * @param delta
     */
    @Override
    public final void update(long delta) {
        int speed = (int) (this.speed * delta);
        for (int i = 0; i < amount; i++) {
            // first layer
            xcoo[i] += (int) (Math.sin(iterator) * 10);
            ycoo[i] += speed;
            if (ycoo[i] >= screenH) {
                ycoo[i] = 0;
            }
            // second layer
            xcoo2[i] += (int) (Math.sin(iterator) * 5);
            ycoo2[i] += speed >> 1;
            if (ycoo2[i] >= screenH) {
                ycoo2[i] = 0;
            }
            // third layer
            xcoo3[i] += (int) (Math.sin(iterator++) * 2);
            ycoo3[i] += speed >> 2;
            if (ycoo3[i] >= screenH) {
                ycoo3[i] = 0;
            }
        }
    }

    /**
     * @see Weather#render(java.awt.Graphics)
     * @param g
     */
    @Override
    public final void render(Graphics g) {
        // first layer
        g.setColor(Color.WHITE);
        for (int i = 0; i < amount; i++) {
            g.fillRect(xcoo[i], ycoo[i], particleSize, particleSize);
        }
        // second layer
        g.setColor(Color.GRAY);
        for (int i = 0; i < amount; i++) {
            g.fillRect(xcoo2[i], ycoo2[i], particleSize >> 1, particleSize >> 1);
        }
        // third layer
        g.setColor(Color.DARK_GRAY);
        for (int i = 0; i < amount; i++) {
            g.fillRect(xcoo3[i], ycoo3[i], particleSize >> 2, particleSize >> 2);
        }
    }

    /**
     * @see Weather#render(java.awt.Graphics, int)
     * @param g
     * @param offscreenX
     */
    @Override
    public final void render(Graphics g, int offscreenX) {
        // first layer
        g.setColor(Color.WHITE);
        for (int i = 0; i < amount; i++) {
            g.fillRect((xcoo[i] + offscreenX) % screenW, ycoo[i], particleSize, particleSize);
        }
        // second layer
        g.setColor(Color.GRAY);
        for (int i = 0; i < amount; i++) {
            g.fillRect((xcoo2[i] + offscreenX) % screenW, ycoo2[i], (particleSize >> 1), (particleSize >> 1));
        }
        // third layer
        g.setColor(Color.DARK_GRAY);
        for (int i = 0; i < amount; i++) {
            g.fillRect((xcoo3[i] + offscreenX) % screenW, ycoo3[i], (particleSize >> 2), (particleSize >> 2));
        }
    }

}
