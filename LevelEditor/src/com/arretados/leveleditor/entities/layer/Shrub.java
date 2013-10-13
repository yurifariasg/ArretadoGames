package com.arretados.leveleditor.entities.layer;

import com.arretados.leveleditor.DrawMode;
import com.arretados.leveleditor.GameCanvas;
import com.arretados.leveleditor.ResourceManager;
import com.arretados.leveleditor.ResourceManager.Resource;
import com.arretados.leveleditor.entities.Entity;
import com.arretados.leveleditor.entities.EntityPanel;
import java.awt.Graphics;
import java.awt.Rectangle;
import org.json.simple.JSONObject;


public class Shrub extends Entity {
    
    private static float[][] SHRUB_SIZES = new float[][] {
        {1f, 0.6f}   
    };
    
    public static EntityPanel shrub_panel;

    //@Override
    public DrawMode getType() {
        return DrawMode.SHRUB;
    }
    
    private int shrubType;

    public Shrub(int x, int y) {
        super(x, y);
        shrubType = 0;
    }

    public int getShrubType() {
        return shrubType;
    }

    public void setShrubType(int shrubType) {
        this.shrubType = shrubType;
    }
    

    @Override
    public boolean collides(int x, int y) {
        Rectangle rect = new Rectangle(
                this.x - ((int) (GameCanvas.METER_TO_PIXELS * SHRUB_SIZES[shrubType][0]/2)),
                this.y - ((int) (GameCanvas.METER_TO_PIXELS * SHRUB_SIZES[shrubType][1]/2)),
                (int) (SHRUB_SIZES[shrubType][0] * GameCanvas.METER_TO_PIXELS),
                (int) (SHRUB_SIZES[shrubType][1] * GameCanvas.METER_TO_PIXELS));
        return rect.contains(x, y);
    }

    @Override
    public void drawMyself(Graphics g) {
        g.drawImage(ResourceManager.getImageFor(Resource.SHRUB),
                x - ((int) (GameCanvas.METER_TO_PIXELS * SHRUB_SIZES[shrubType][0]/2)),
                y - ((int) (GameCanvas.METER_TO_PIXELS * SHRUB_SIZES[shrubType][1]/2)),
                (int) (SHRUB_SIZES[shrubType][0] * GameCanvas.METER_TO_PIXELS),
                (int) (SHRUB_SIZES[shrubType][1] * GameCanvas.METER_TO_PIXELS), null);
    }

    @Override
    public JSONObject toJSON() {
        JSONObject json = super.toJSON();
        json.put("type", "shrub");
        json.put("shrubType", shrubType);
        return json;
    }

    @Override
    public EntityPanel getEntityPanel() {
        return shrub_panel;
    }
    
}
