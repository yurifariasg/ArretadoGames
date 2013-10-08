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
public class Breakable extends Entity{
    
    public static BreakablePanel breakable_panel;
    
    private float width;
    private float height;
    private float hitsUntilBreak;
    
    public Breakable(int x, int y){
        super(x, y);
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public float getHitsUntilBreak() {
        return hitsUntilBreak;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public void setHitsUntilBreak(float hitsUntilBreak) {
        this.hitsUntilBreak = hitsUntilBreak;
    }

    @Override
    public boolean collides(int x, int y) {
        Rectangle rect = new Rectangle(
                this.x - ((int) (GameCanvas.METER_TO_PIXELS * this.width/2)),
                this.y - ((int) (GameCanvas.METER_TO_PIXELS * this.height/2)),
                (int) (this.width * GameCanvas.METER_TO_PIXELS),
                (int) (this.height * GameCanvas.METER_TO_PIXELS));
        return rect.contains(x, y);
    }

    @Override
    public void drawMyself(Graphics g) {
        g.drawImage(ResourceManager.getImageFor(DrawMode.BREAKABLE),
                x - ((int) (GameCanvas.METER_TO_PIXELS * this.width/2)),
                y - ((int) (GameCanvas.METER_TO_PIXELS * this.height/2)),
                (int) (this.width * GameCanvas.METER_TO_PIXELS),
                (int) (this.height * GameCanvas.METER_TO_PIXELS), null);
    }

    @Override
    public JSONObject toJSON() {
        JSONObject json = super.toJSON();
        json.put("type", "box");
        json.put("width", width);
        json.put("height", height);
        json.put("hitsUntilBreak", hitsUntilBreak);
        return json;
    }

    @Override
    public EntityPanel getEntityPanel() {
        return breakable_panel;
    }
    
}
