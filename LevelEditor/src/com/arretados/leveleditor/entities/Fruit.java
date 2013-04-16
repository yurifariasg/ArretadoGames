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
public class Fruit extends DrawableObject{
    
    private int x;
    private int y;
    //private Graphics g;

    public Fruit(int x, int y) {
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
        g.drawOval(x-12, y-12, 25, 25);
    }    
}
