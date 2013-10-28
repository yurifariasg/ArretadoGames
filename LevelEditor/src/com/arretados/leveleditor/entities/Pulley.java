/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.arretados.leveleditor.entities;

import com.arretados.leveleditor.DrawMode;
import com.arretados.leveleditor.GameCanvas;
import java.awt.Color;
import java.awt.Graphics;
import org.json.simple.JSONObject;

/**
 *
 * @author Bruno
 */
public class Pulley extends Entity{
    
    public static EntityPanel pulley_panel;
    
    private int size;
    
    public Pulley(int x, int y, int size){
        super(x, y, DrawMode.PULLEY);
        this.size = size;
    }
    
    public Pulley(JSONObject json){
        super((int) (Double.parseDouble(String.valueOf(json.get("x"))) * GameCanvas.METER_TO_PIXELS),
              (int) (Double.parseDouble(String.valueOf(json.get("y"))) * GameCanvas.METER_TO_PIXELS),
              DrawMode.PULLEY);
        
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
        g.setColor(Color.orange);
        g.fillRect(x-(this.size/2), y-(this.size/2), this.size, this.size);
        g.fillRect(x-(this.size), y-(this.size/2)+100, (this.size/4), (this.size/4));
        g.setColor(Color.black);
        g.fillRect(x-(this.size)+10, y-(this.size/2)-25, 6, 125);
        g.fillRect(x-(this.size)+10, y-(this.size/2)-25, 100, 6);
        g.fillRect(x-(this.size/2)+56, y-(this.size/2)-25,  6, 25);
    }

    @Override
    public JSONObject toJSON() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public EntityPanel getEntityPanel() {
        return pulley_panel;
    }
    
}
