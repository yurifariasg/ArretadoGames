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


public class Grass extends Entity {
    
    private static float[][] GRASS_SIZES = new float[][] {
        {0.4f, 0.4f}
    };
    
    public static EntityPanel grass_panel;

    //@Override
    public DrawMode getType() {
        return DrawMode.GRASS;
    }
    
    private int grassType;

    public Grass(int x, int y) {
        super(x, y, DrawMode.GRASS);
        grassType = 0;
    }
    
    public Grass(JSONObject json){
        super((int) (Double.parseDouble(String.valueOf(json.get("x"))) * GameCanvas.METER_TO_PIXELS),
              (int) (Double.parseDouble(String.valueOf(json.get("y"))) * GameCanvas.METER_TO_PIXELS),
              DrawMode.GRASS);
    }            

    public int getGrassType() {
        return grassType;
    }

    public void setGrassType(int grassType) {
        this.grassType = grassType;
    }
    

    @Override
    public boolean collides(int x, int y) {
        Rectangle rect = new Rectangle(
                this.x - ((int) (GameCanvas.METER_TO_PIXELS * GRASS_SIZES[grassType][0]/2)),
                this.y - ((int) (GameCanvas.METER_TO_PIXELS * GRASS_SIZES[grassType][1]/2)),
                (int) (GRASS_SIZES[grassType][0] * GameCanvas.METER_TO_PIXELS),
                (int) (GRASS_SIZES[grassType][1] * GameCanvas.METER_TO_PIXELS));
        return rect.contains(x, y);
    }

    @Override
    public void drawMyself(Graphics g) {
        g.drawImage(ResourceManager.getImageFor(Resource.GRASS),
                x - ((int) (GameCanvas.METER_TO_PIXELS * GRASS_SIZES[grassType][0]/2)),
                y - ((int) (GameCanvas.METER_TO_PIXELS * GRASS_SIZES[grassType][1]/2)),
                (int) (GRASS_SIZES[grassType][0] * GameCanvas.METER_TO_PIXELS),
                (int) (GRASS_SIZES[grassType][1] * GameCanvas.METER_TO_PIXELS), null);
    }

    @Override
    public JSONObject toJSON() {
        JSONObject json = super.toJSON();
        json.put("type", this.type.toString());
        json.put("grassType", grassType);
        return json;
    }

    @Override
    public EntityPanel getEntityPanel() {
        return grass_panel;
    }
    
}
