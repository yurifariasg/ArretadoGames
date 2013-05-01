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
    private int size;
    
    public Box(int x, int y, int size) {
        this.x = x;
        this.y = y;
        this.size = size;
    }
    
    public int getX(){
        return this.x;
    }
    
    public int getY(){
        return this.y;
    }
    
    public int getSize(){
        return this.size;
    }

    @Override
    public void drawMyself(Graphics g) {
        g.drawRect(x-(this.size/2), y-(this.size/2), this.size, this.size);
    }    
}