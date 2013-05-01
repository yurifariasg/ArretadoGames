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
public class Box extends DrawableObject{
    
    private int x;
    private int y;
    
    public Box(int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    public int getX(){
        return this.x;
    }
    
    public int getY(){
        return this.y;
    }

    @Override
    public void drawMyself(Graphics g) {
        g.drawRect(x-25, y-25, 50, 50);
    }    
}