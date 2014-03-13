/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.arretados.leveleditor.entities;

import com.arretados.leveleditor.DrawMode;
import com.arretados.leveleditor.Utils;
import java.awt.Graphics;
import org.json.simple.JSONObject;

/**
 *
 * @author Bruno
 */
public abstract class Entity {
    
    protected int x, y;
    protected DrawMode type;
    
    public Entity(int x, int y, DrawMode type) {
        this.x = x;
        this.y = y;
        this.type = type;
    }
    
    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }
    
    public abstract void drawMyself(Graphics g);

    public boolean collides(int x, int y) {
        return false; // TODO: create collides
    }

    public DrawMode getType() {
        return type;
    }
    
    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        json.put("x", Utils.convertPixelToMeter(x));
        json.put("y", Utils.convertPixelToMeter(y));
        return json;
    }

    public EntityPanel getEntityPanel() { return null; }
    
}
