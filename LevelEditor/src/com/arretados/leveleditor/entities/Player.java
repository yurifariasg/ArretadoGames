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
    
    public void setX(int x){
        this.x = x;
    }
    
    public void setY(int y){
        this.y = y;
    }
    
    public int getSize(){
        return this.size;
    }
    
    public String getPlayer(){
        return this.player;
    }

    @Override
    public void drawMyself(Graphics g) {
        if (this.player.equals("player1"))
            g.setColor(Color.blue);
        else
            g.setColor(Color.gray);
            
        g.fillRect(x-(this.size/2), y-(this.size/2), this.size, this.size);
    }    
}
