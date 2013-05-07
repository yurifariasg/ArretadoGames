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
public class Player extends DrawableObject{
       
    private int x;
    private int y;
    private int size;
    private String player;
    
    public Player(int x, int y, int size, String player){
        this.x = x;
        this.y = y;
        this.size = size;
        this.player = player;
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
    
    public String getPlayer(){
        return this.player;
    }

    @Override
    public void drawMyself(Graphics g) {
        g.drawRect(x-(this.size/2), y-(this.size/2), this.size, this.size);
    }    
}
