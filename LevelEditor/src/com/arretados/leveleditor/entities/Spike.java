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
public class Spike extends Entity{
    
    public static SpikePanel spike_panel;
    
    private float size;
    private float weight;
    
    public Spike(int x, int y, float size) {
        super(x, y, DrawMode.SPIKE);
        this.size = size;
    }

    public Spike(JSONObject json){
        super((int) (Double.parseDouble(String.valueOf(json.get("x"))) * GameCanvas.METER_TO_PIXELS),
              (int) (Double.parseDouble(String.valueOf(json.get("y"))) * GameCanvas.METER_TO_PIXELS),
              DrawMode.SPIKE);
        
        this.setSize( ((Double) Double.parseDouble( String.valueOf(json.get("size")))).floatValue() );
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
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
        g.drawImage(ResourceManager.getImageFor(Resource.SPIKE),
                x - ((int) (GameCanvas.METER_TO_PIXELS * this.size/2)),
                y - ((int) (GameCanvas.METER_TO_PIXELS * this.size/2)),
                (int) (this.size * GameCanvas.METER_TO_PIXELS),
                (int) (this.size * GameCanvas.METER_TO_PIXELS), null);
    }

    @Override
    public JSONObject toJSON() {
        JSONObject json = super.toJSON();
        json.put("type", this.type.toString());
        json.put("size", size);
        return json;
    }

    @Override
    public EntityPanel getEntityPanel() {
        return spike_panel;
    }

    
}