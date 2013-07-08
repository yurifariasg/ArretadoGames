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
public class OneWayWall extends DrawableObject{
    
    private int x;
    private int y;
    private int size;
    
    public OneWayWall(int x, int y, int size) {
        this.x = x;
        this.y = y;
        this.size = size;
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

    public void drawMyself(Graphics g) {
        g.setColor(Color.black);
        g.fillRect(x-(this.size/2), y-10, this.size, 10);
        g.setColor(Color.green);
        g.fillRect(x-(this.size/2), y, this.size, 10);
    }    
    
}
