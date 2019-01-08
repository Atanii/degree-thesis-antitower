/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.emanuel.jeremi.antitower.effect;

import java.awt.Graphics;

/**
 * Class representing a "generic weather". All of the weather effects in the game consist of generating - updating - rendering and use the same variables,
 * but different configurations.
 * 
 * This class work like some kinf of factory. The user don't have to import the specific classes, the Weather is perfectly enough.
 * 
 * @author Jeremi
 */
public abstract class Weather {
    
    // <editor-fold defaultstate="collapsed" desc="variables, constants">
    int amount;
    int particleSize;
    int seed;
    
    int[] xcoo;
    int[] ycoo;
    int[] xcoo2;
    int[] ycoo2;
    int[] xcoo3;
    int[] ycoo3;
    int iterator;
    int speed;
    int screenH, screenW;
    // </editor-fold>
    
    public Weather(final int screenW, final int screenH) {
        this.screenW = screenW;
        this.screenH = screenH;
        config();
    }
    
    /**
     * Give a new Snow object.
     * @param screenW
     * @param screenH
     * @return 
     */
    public final static Snow getSnow(final int screenW, final int screenH) {
        return new Snow(screenW, screenH);
    }
    
    /**
     * Give a new Rain object.
     * @param screenW
     * @param screenH
     * @return 
     */
    public final static Rain getRain(final int screenW, final int screenH) {
        return new Rain(screenW, screenH);
    }
    
    /**
     * Set the values of the variables.
     */
    public abstract void config();
    
    /**
     * Generates the particles of the weather effects in the given amount (amount variable).
     */
    public abstract void generate();
    /**
     * Updates the weather particles, by supposedly moving them. The delta is for the constant speed.
     * @param delta 
     */
    public abstract void update(long delta);
    /**
     * Render a static weather effects which doesn't rotate when the player turns around.
     * @param g 
     */
    public abstract void render(Graphics g);
    /**
     * Render a static weather effects which rotates when the player turns around.
     * @param g
     * @param offscreenX 
     */
    public abstract void render(Graphics g, int offscreenX);
    
}
