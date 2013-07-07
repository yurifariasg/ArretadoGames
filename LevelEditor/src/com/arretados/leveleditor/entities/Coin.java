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
public class Coin extends DrawableObject{
    
    private int x;
    private int y;
    private int size;

    public Coin(int x, int y, int size) {
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
    
    public void setX(int x){
        this.x = x;
    }
    
    public void setY(int y){
        this.y = y;
    }
    
    public int getSize(){
        return this.size;
    }

    @Override
    public void drawMyself(Graphics g) {
        g.setColor(Color.yellow);
        g.fillOval(x-(this.size/2), y-(this.size/2), this.size, this.size);
    }    
}