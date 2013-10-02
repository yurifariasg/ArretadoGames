/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.arretados.leveleditor.entities;

import com.arretados.leveleditor.GameCanvas;
import java.awt.Color;
import java.awt.Graphics;
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
    public void drawMyself(Graphics g) {
        g.setColor(Color.blue);
        g.fillRect(
                (int) (x - (width/2) * GameCanvas.METER_TO_PIXELS),
                (int) (y - (height/2) * GameCanvas.METER_TO_PIXELS),
                (int) (width * GameCanvas.METER_TO_PIXELS),
                (int) (height * GameCanvas.METER_TO_PIXELS));
    }

    @Override
    public JSONObject toJSON() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public EntityPanel getEntityPanel() {
        return fluid_panel;
    }
}
