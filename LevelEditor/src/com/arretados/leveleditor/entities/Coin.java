/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.arretados.leveleditor.entities;

import com.arretados.leveleditor.DrawMode;
import com.arretados.leveleditor.ResourceManager;
import java.awt.Color;
import java.awt.Graphics;

/**
 *
 * @author Bruno
 */
public class Coin extends DrawableObject{
    
    private int size;

    public Coin(int x, int y, int size) {
        super(x, y);
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
        g.drawImage(ResourceManager.getImageFor(DrawMode.COIN), x-(this.size/2), y-(this.size/2), this.size, this.size, null);
    }    
}