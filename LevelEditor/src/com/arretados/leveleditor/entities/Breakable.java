/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.arretados.leveleditor.entities;

import java.awt.Color;
import java.awt.Graphics;

/**
 *
 * @author Bruno
 */
public class Breakable extends DrawableObject{
    private int x;
    private int y;
    private int size;
    
    public Breakable(int x, int y){
        this.x = x;
        this.y = y;
    }
    
    public int getX(){
        return this.x;
    }
    
    public void setX(int x){
        this.x = x;
    }
    
    public int getY(){
        return this.y;
    }
    
    public void setY(int y){
        this.y = y;
    }
    
    public int getSize(){
        return this.size;
    }

    @Override
    public void drawMyself(Graphics g) {
        g.setColor(Color.gray);
        g.drawRect(this.x-5, this.y-25, 10, 100);
    }
    
}
