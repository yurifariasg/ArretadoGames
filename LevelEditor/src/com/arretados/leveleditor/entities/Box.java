/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.arretados.leveleditor.entities;

import com.arretados.leveleditor.DrawMode;
import com.arretados.leveleditor.GameCanvas;
import com.arretados.leveleditor.ResourceManager;
import java.awt.Graphics;
import java.awt.Rectangle;
import org.json.simple.JSONObject;

/**
 *
 * @author Bruno
 */
public class Box extends Entity{
    
    public static BoxPanel box_panel;
    
    private float size;
    
    public Box(int x, int y, float size) {
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
    
    public float getSize(){
        return this.size;
    }

    public void setSize(float size) {
        this.size = size;
    }

    @Override
    public boolean collides(int x, int y) {
        Rectangle rect = new Rectangle(
                this.x - ((int) (GameCanvas.METER_TO_PIXELS * this.size/2)),
                this.y - ((int) (GameCanvas.METER_TO_PIXELS * this.size/2)),
                (int) (this.size * GameCanvas.METER_TO_PIXELS),
                (int) (this.size * GameCanvas.METER_TO_PIXELS));
        return rect.contains(x, y);
    }

    @Override
    public void drawMyself(Graphics g) {
        g.drawImage(ResourceManager.getImageFor(DrawMode.BOX),
                x - ((int) (GameCanvas.METER_TO_PIXELS * this.size/2)),
                y - ((int) (GameCanvas.METER_TO_PIXELS * this.size/2)),
                (int) (this.size * GameCanvas.METER_TO_PIXELS),
                (int) (this.size * GameCanvas.METER_TO_PIXELS), null);
    }

    @Override
    public JSONObject toJSON() {
        JSONObject json = super.toJSON();
        json.put("type", "box");
        json.put("size", size);
        return json;
    }

    @Override
    public EntityPanel getEntityPanel() {
        return box_panel;
    }
    
}