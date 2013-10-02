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
import org.json.simple.JSONObject;

/**
 *
 * @author Bruno
 */
public class Player extends Entity{
       
    private int size;
    private String player;
    
    public Player(int x, int y, int size, String player){
        super(x, y);
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
        Image img = null;
        if (this.player.equals("player1"))
            img = ResourceManager.getImageFor(DrawMode.P1);
        else
            img = ResourceManager.getImageFor(DrawMode.P2);
            
        g.drawImage(img, x-(this.size/2), y-(this.size/2), this.size, this.size, null);
    }

    @Override
    public JSONObject toJSON() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
