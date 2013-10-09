/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.arretados.leveleditor.entities;

import com.arretados.leveleditor.GameCanvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import org.json.simple.JSONObject;

/**
 *
 * @author Bruno
 */
public class Fluid extends Entity{
    
    public static EntityPanel fluid_panel;

    private float width;
    private float height;
    private float density;
    
    public Fluid(int x, int y) {
        super(x, y);
    }

    public float getDensity() {
        return density;
    }

    public void setDensity(float density) {
        this.density = density;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
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
    public JSONObject toJSON() {
        JSONObject json = super.toJSON();
        json.put("type", "fluid");
        json.put("width", width);
        json.put("height", height);
        return json;
    }

    @Override
    public void drawMyself(Graphics g) {
        g.setColor(Color.blue);
        g.fillRect(
                (int) (x - (width/2) * GameCanvas.METER_TO_PIXELS),
                (int) (y - (height/2) * GameCanvas.METER_TO_PIXELS),
                (int) (width * GameCanvas.METER_TO_PIXELS),
                (int) (height * GameCanvas.METER_TO_PIXELS));
    }

    @Override
    public EntityPanel getEntityPanel() {
        return fluid_panel;
    }
}
