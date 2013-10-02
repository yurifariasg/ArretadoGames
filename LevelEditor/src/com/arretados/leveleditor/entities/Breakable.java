/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.arretados.leveleditor.entities;

import com.arretados.leveleditor.DrawMode;
import com.arretados.leveleditor.ResourceManager;
import java.awt.Graphics;
import org.json.simple.JSONObject;

/**
 *
 * @author Bruno
 */
public class Breakable extends Entity{
    
    public static BreakablePanel breakable_panel;
    
    private float width;
    private float height;
    private float hitsUntilBreak;
    
    public Breakable(int x, int y){
        super(x, y);
    }
    
    public int getX(){
        return this.x;
    }
    
    public void setX(int x){
        this.x = x;
    }
    
    public int getY(){
        return this.y;
    }
    
    public void setY(int y){
        this.y = y;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public float getHitsUntilBreak() {
        return hitsUntilBreak;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public void setHitsUntilBreak(float hitsUntilBreak) {
        this.hitsUntilBreak = hitsUntilBreak;
    }

    @Override
    public void drawMyself(Graphics g) {
        g.drawImage(
                ResourceManager.getImageFor(DrawMode.BREAKABLE),
                this.x-5, this.y-100,
                10, 200,
                null);
    }

    @Override
    public JSONObject toJSON() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public EntityPanel getEntityPanel() {
        return breakable_panel;
    }
    
}
