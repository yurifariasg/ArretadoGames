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
import java.awt.Image;
import java.awt.Rectangle;
import org.json.simple.JSONObject;

/**
 *
 * @author Bruno
 */
public class Player extends Entity{
    
    private String player;
    
    public Player(int x, int y, String player){
        super(x, y);
        this.player = player;
    }
    
    public String getPlayer(){
        return this.player;
    }

    @Override
    public boolean collides(int x, int y) {
        Rectangle rect = new Rectangle(
                (int) (this.x-(GameCanvas.METER_TO_PIXELS/2)),
                (int) (this.y-(GameCanvas.METER_TO_PIXELS/2)),
                (int) (GameCanvas.METER_TO_PIXELS),
                (int) (GameCanvas.METER_TO_PIXELS));
        return rect.contains(x, y);
    }

    @Override
    public void drawMyself(Graphics g) {
        Image img = null;
        if (this.player.equals("player1"))
            img = ResourceManager.getImageFor(Resource.P1);
        else
            img = ResourceManager.getImageFor(Resource.P2);
            
        g.drawImage(
                img,
                (int) (x-(GameCanvas.METER_TO_PIXELS/2)),
                (int) (y-(GameCanvas.METER_TO_PIXELS/2)),
                (int) (GameCanvas.METER_TO_PIXELS),
                (int) (GameCanvas.METER_TO_PIXELS),
                null);
    }

    @Override
    public JSONObject toJSON() {
        JSONObject json = super.toJSON();
        json.put("type", "player");
        json.put("number", this.player.equals("player1") ? 1 : 2 );
        return json;
    }
}
