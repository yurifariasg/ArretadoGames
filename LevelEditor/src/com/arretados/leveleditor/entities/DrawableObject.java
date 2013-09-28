/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.arretados.leveleditor.entities;

import java.awt.Graphics;

/**
 *
 * @author Bruno
 */
public abstract class DrawableObject {
    
    protected int x, y;
    
    public DrawableObject(int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    public abstract void drawMyself(Graphics g);
    
}
