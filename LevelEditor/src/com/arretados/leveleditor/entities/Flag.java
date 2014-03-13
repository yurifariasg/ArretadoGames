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
public class Flag extends Entity{
    
    public final static float SIZE = 1.5f;
    
    public Flag(int x, int y, int size) {
        super(x, y, DrawMode.FLAG);
    }
    
    public Flag(JSONObject json){
        super((int) (Double.parseDouble(String.valueOf(json.get("x"))) * GameCanvas.METER_TO_PIXELS),
              (int) (Double.parseDouble(String.valueOf(json.get("y"))) * GameCanvas.METER_TO_PIXELS),
              DrawMode.FLAG);        
    }

    public void drawMyself(Graphics g) {
        //g.setColor(Color.black);
        //g.fillRect((int) (x-(SIZE/2)), (int) (y-(SIZE/2)), (int) SIZE, 100);
        //g.setColor(Color.red);
        //g.fillRect((int) (x-(SIZE/2)), y, 60, (int) (SIZE*2));
        
        g.drawImage(ResourceManager.getImageFor(Resource.FLAG),
                x - ((int) (GameCanvas.METER_TO_PIXELS * SIZE/2)),
                y - ((int) (GameCanvas.METER_TO_PIXELS * SIZE/2)),
                (int) (SIZE * GameCanvas.METER_TO_PIXELS),
                (int) (SIZE * GameCanvas.METER_TO_PIXELS), null);
    }

    @Override
    public JSONObject toJSON() {
        JSONObject json = super.toJSON();
        json.put("type", this.type.toString());
        return json;
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
    
}
