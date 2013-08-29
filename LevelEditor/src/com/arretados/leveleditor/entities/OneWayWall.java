/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.arretados.leveleditor.entities;

import com.arretados.leveleditor.DrawMode;
import com.arretados.leveleditor.ResourceManager;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;

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
        g.drawImage(ResourceManager.getImageFor(DrawMode.ONEWAY_WALL), x - 106, y - 10, 212, 112, null);
    }    
    
}
