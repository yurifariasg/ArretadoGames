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


public class BoxItem extends Entity{
    
    public static BoxItemPanel boxItem_panel;
    
    public BoxItem(int x, int y) {
        super(x, y, DrawMode.BOX_ITEM);
    }

    public BoxItem(JSONObject json){
        super((int) (Double.parseDouble(String.valueOf(json.get("x"))) * GameCanvas.METER_TO_PIXELS),
              (int) (Double.parseDouble(String.valueOf(json.get("y"))) * GameCanvas.METER_TO_PIXELS),
              DrawMode.BOX_ITEM);
    }
    
    @Override
    public boolean collides(int x, int y) {
        Rectangle rect = new Rectangle(
                this.x - ((int) (GameCanvas.METER_TO_PIXELS * 0.5f)),
                this.y - ((int) (GameCanvas.METER_TO_PIXELS * 0.5f)),
                (int) (GameCanvas.METER_TO_PIXELS),
                (int) (GameCanvas.METER_TO_PIXELS));
        return rect.contains(x, y);
    }

    @Override
    public void drawMyself(Graphics g) {
        g.drawImage(ResourceManager.getImageFor(Resource.BOX),
                x - ((int) (GameCanvas.METER_TO_PIXELS * 0.5f)),
                y - ((int) (GameCanvas.METER_TO_PIXELS * 0.5f)),
                (int) (GameCanvas.METER_TO_PIXELS),
                (int) (GameCanvas.METER_TO_PIXELS),
                null);
        
        g.drawImage(ResourceManager.getImageFor(Resource.QUESTION_ITEM),
                x - ((int) (GameCanvas.METER_TO_PIXELS * 0.5f)),
                y - ((int) (GameCanvas.METER_TO_PIXELS * 0.5f)),
                (int) (GameCanvas.METER_TO_PIXELS),
                (int) (GameCanvas.METER_TO_PIXELS),
                null);
    }

    @Override
    public JSONObject toJSON() {
        JSONObject json = super.toJSON();
        json.put("type", this.type.toString());
        return json;
    }

    @Override
    public EntityPanel getEntityPanel() {
        return boxItem_panel;
    }
    
}