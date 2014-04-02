/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.arretados.leveleditor.entities;

import com.arretados.leveleditor.DrawMode;
import com.arretados.leveleditor.GameCanvas;
import com.arretados.leveleditor.ResourceManager;
import com.arretados.leveleditor.ResourceManager.Resource;
import java.awt.Graphics;
import java.awt.Rectangle;
import org.json.simple.JSONObject;

/**
 *
 * @author Bruno
 */
public final class Hole extends Entity{
    
    private float HOLE_WIDTH = 1.3f;
    private float HOLE_HEIGHT = 0.4f;
    
    public static HolePanel panel;
    
    private float distance;
    
    public Hole(int x, int y) {
        this(x, y, 3f);
    }
    
    public Hole(int x, int y, float distance) {
        super(x, y, DrawMode.HOLE);
        this.distance = distance;
    }

    public Hole(JSONObject json){
        super((int) (Double.parseDouble(String.valueOf(json.get("x"))) * GameCanvas.METER_TO_PIXELS),
              (int) (Double.parseDouble(String.valueOf(json.get("y"))) * GameCanvas.METER_TO_PIXELS),
              DrawMode.BOX);
        
        this.setDistance( ((Double) Double.parseDouble( String.valueOf(json.get("distance")))).floatValue() );
    }
    
    public float getDistance(){
        return this.distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }
    
    @Override
    public boolean collides(int x, int y) {
        Rectangle rect = new Rectangle(
                this.x - (int) ((GameCanvas.METER_TO_PIXELS * (HOLE_WIDTH - distance) /2)),
                this.y - (int) ((GameCanvas.METER_TO_PIXELS * HOLE_HEIGHT/2)),
                (int) (HOLE_WIDTH * GameCanvas.METER_TO_PIXELS),
                (int) (HOLE_HEIGHT * GameCanvas.METER_TO_PIXELS));
        
        if (rect.contains(x, y)) {
            return true;
        }
        
        rect = new Rectangle(
                this.x - (int) ((GameCanvas.METER_TO_PIXELS * (HOLE_WIDTH + distance) /2)),
                this.y - (int) ((GameCanvas.METER_TO_PIXELS * HOLE_HEIGHT/2)),
                (int) (HOLE_WIDTH * GameCanvas.METER_TO_PIXELS),
                (int) (HOLE_HEIGHT * GameCanvas.METER_TO_PIXELS));
        
        return rect.contains(x, y);
    }

    @Override
    public void drawMyself(Graphics g) {
        g.drawImage(ResourceManager.getImageFor(Resource.HOLE),
                x - ((int) (GameCanvas.METER_TO_PIXELS * (HOLE_WIDTH - distance) / 2)),
                y - ((int) (GameCanvas.METER_TO_PIXELS * (HOLE_HEIGHT) /2)),
                (int) (HOLE_WIDTH * GameCanvas.METER_TO_PIXELS),
                (int) (HOLE_HEIGHT * GameCanvas.METER_TO_PIXELS), null);
        
        g.drawImage(ResourceManager.getImageFor(Resource.HOLE),
                x - ((int) (GameCanvas.METER_TO_PIXELS * (HOLE_WIDTH + distance) / 2)),
                y - ((int) (GameCanvas.METER_TO_PIXELS * (HOLE_HEIGHT) /2)),
                (int) (HOLE_WIDTH * GameCanvas.METER_TO_PIXELS),
                (int) (HOLE_HEIGHT * GameCanvas.METER_TO_PIXELS), null);
    }

    @Override
    public JSONObject toJSON() {
        JSONObject json = super.toJSON();
        json.put("type", this.type.toString());
        json.put("distance", distance);
        return json;
    }

    @Override
    public EntityPanel getEntityPanel() {
        return panel;
    }
    
}