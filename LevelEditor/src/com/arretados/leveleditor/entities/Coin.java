/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.arretados.leveleditor.entities;

import com.arretados.leveleditor.DrawMode;
import com.arretados.leveleditor.GameCanvas;
import com.arretados.leveleditor.ResourceManager;
import java.awt.Graphics;
import org.json.simple.JSONObject;

/**
 *
 * @author Bruno
 */
public class Coin extends Entity{
    
    public static EntityPanel coin_panel;
    
    private int value;

    public Coin(int x, int y) {
        super(x, y);
    }
    
    public int getX(){
        return this.x;
    }
    
    public int getY(){
        return this.y;
    }
    
    public void setX(int x){
        this.x = x;
    }
    
    public void setY(int y){
        this.y = y;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    @Override
    public void drawMyself(Graphics g) {
        g.drawImage(ResourceManager.getImageFor(DrawMode.COIN),
                x - (int) (GameCanvas.METER_TO_PIXELS * 0.5 / 2), y - (int) (GameCanvas.METER_TO_PIXELS * 0.5 / 2),
                (int) (GameCanvas.METER_TO_PIXELS * 0.5), (int) (GameCanvas.METER_TO_PIXELS * 0.5),
                null);
    }

    @Override
    public JSONObject toJSON() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public EntityPanel getEntityPanel() {
        return coin_panel;
    }
}