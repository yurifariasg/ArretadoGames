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
public class Coin extends Entity{
    
    public static float SIZE = 0.4f;
    
    public static EntityPanel coin_panel;
    
    private int value;

    public Coin(int x, int y) {
        super(x, y);
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    @Override
    public boolean collides(int x, int y) {
        Rectangle rect = new Rectangle(
                this.x - ((int) (GameCanvas.METER_TO_PIXELS * SIZE/2)),
                this.y - ((int) (GameCanvas.METER_TO_PIXELS * SIZE/2)),
                (int) (SIZE * GameCanvas.METER_TO_PIXELS),
                (int) (SIZE * GameCanvas.METER_TO_PIXELS));
        return rect.contains(x, y);
    }

    @Override
    public void drawMyself(Graphics g) {
        g.drawImage(ResourceManager.getImageFor(DrawMode.COIN),
                x - ((int) (GameCanvas.METER_TO_PIXELS * SIZE/2)),
                y - ((int) (GameCanvas.METER_TO_PIXELS * SIZE/2)),
                (int) (SIZE * GameCanvas.METER_TO_PIXELS),
                (int) (SIZE * GameCanvas.METER_TO_PIXELS), null);
    }

    @Override
    public JSONObject toJSON() {
        JSONObject json = super.toJSON();
        json.put("type", "coin");
        json.put("value", value);
        return json;
    }

    @Override
    public EntityPanel getEntityPanel() {
        return coin_panel;
    }
}