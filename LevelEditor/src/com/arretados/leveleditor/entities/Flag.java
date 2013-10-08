/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.arretados.leveleditor.entities;

import java.awt.Color;
import java.awt.Graphics;
import org.json.simple.JSONObject;

/**
 *
 * @author Bruno
 */
public class Flag extends Entity{
    
    public final static float SIZE = 0.5f;
    
    public Flag(int x, int y, int size) {
        super(x, y);
    }

    public void drawMyself(Graphics g) {
        g.setColor(Color.black);
        g.fillRect((int) (x-(SIZE/2)), (int) (y-(SIZE/2)), (int) SIZE, 100);
        g.setColor(Color.red);
        g.fillRect((int) (x-(SIZE/2)), y, 60, (int) (SIZE*2));
    }

    @Override
    public JSONObject toJSON() {
        JSONObject json = super.toJSON();
        json.put("type", "flag");
        return json;
    }
    
}
