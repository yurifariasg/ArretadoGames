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
public class OneWayWall extends Entity{
    
    public static EntityPanel onewaywall_panel;
    
    private int size;
    
    public OneWayWall(int x, int y, int size) {
        super(x, y);
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

    @Override
    public JSONObject toJSON() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public EntityPanel getEntityPanel() {
        return onewaywall_panel;
    }
    
}
