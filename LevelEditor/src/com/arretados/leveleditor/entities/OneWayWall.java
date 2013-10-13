/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.arretados.leveleditor.entities;

import com.arretados.leveleditor.DrawMode;
import com.arretados.leveleditor.GameCanvas;
import com.arretados.leveleditor.ResourceManager;
import com.arretados.leveleditor.ResourceManager.Resource;
import com.sun.xml.internal.bind.v2.util.CollisionCheckStack;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import org.json.simple.JSONObject;

/**
 *
 * @author Bruno
 */
public class OneWayWall extends Entity{
    
    public static EntityPanel onewaywall_panel;
    
    private float width;
    private float height;
    
    public OneWayWall(int x, int y, float width, float height) {
        super(x, y);
        this.width = width;
        this.height = height;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public void setWidth(float width) {
        this.width = width;
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

    public void drawMyself(Graphics g) { // 3.0, 0.1
	g.drawImage(ResourceManager.getImageFor(Resource.ONEWAY_WALL),
                x + (int) (- width/2 * GameCanvas.METER_TO_PIXELS), // Top Left X
                y + (int) ((- height/2 - 0.3f) * GameCanvas.METER_TO_PIXELS), // Top Left Y
                (int) (width * GameCanvas.METER_TO_PIXELS), // Bottom Right X
                (int) ((height + 2) * GameCanvas.METER_TO_PIXELS), // Bottom Right Y
                null);
        g.setColor(Color.RED);
        g.drawRect(x - ((int) (GameCanvas.METER_TO_PIXELS * this.width/2)),
                y - ((int) (GameCanvas.METER_TO_PIXELS * this.height/2)),
                (int) (this.width * GameCanvas.METER_TO_PIXELS),
                (int) (this.height * GameCanvas.METER_TO_PIXELS));
    }

    @Override
    public JSONObject toJSON() {
        JSONObject json = super.toJSON();
        json.put("type", "onewaywall");
        json.put("width", width);
        json.put("height", height);
        return json;
    }

    @Override
    public EntityPanel getEntityPanel() {
        return onewaywall_panel;
    }
    
}
