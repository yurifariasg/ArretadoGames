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


public class Tree extends Entity {
    
    private static float[][] TREE_SIZES = new float[][] {
        {3, 4},
        {3, 4}    
    };
    
    public static EntityPanel tree_panel;

    //@Override
    public DrawMode getType() {
        return DrawMode.TREE;
    }
    
    private int treeType;

    public Tree(int x, int y) {
        super(x, y, DrawMode.TREE);
        treeType = 0;
    }
    
    public Tree(JSONObject json){
        super((int) (Double.parseDouble(String.valueOf(json.get("x"))) * GameCanvas.METER_TO_PIXELS),
              (int) (Double.parseDouble(String.valueOf(json.get("y"))) * GameCanvas.METER_TO_PIXELS),
              DrawMode.TREE);        
    }

    public int getTreeType() {
        return treeType;
    }

    public void setTreeType(int treeType) {
        this.treeType = treeType;
    }
    

    @Override
    public boolean collides(int x, int y) {
        Rectangle rect = new Rectangle(
                this.x - ((int) (GameCanvas.METER_TO_PIXELS * TREE_SIZES[treeType][0]/2)),
                this.y - ((int) (GameCanvas.METER_TO_PIXELS * TREE_SIZES[treeType][1]/2)),
                (int) (TREE_SIZES[treeType][0] * GameCanvas.METER_TO_PIXELS),
                (int) (TREE_SIZES[treeType][1] * GameCanvas.METER_TO_PIXELS));
        return rect.contains(x, y);
    }

    @Override
    public void drawMyself(Graphics g) {
        g.drawImage(ResourceManager.getImageFor(Resource.TREE1),
                x - ((int) (GameCanvas.METER_TO_PIXELS * TREE_SIZES[treeType][0]/2)),
                y - ((int) (GameCanvas.METER_TO_PIXELS * TREE_SIZES[treeType][1]/2)),
                (int) (TREE_SIZES[treeType][0] * GameCanvas.METER_TO_PIXELS),
                (int) (TREE_SIZES[treeType][1] * GameCanvas.METER_TO_PIXELS), null);
    }

    @Override
    public JSONObject toJSON() {
        JSONObject json = super.toJSON();
        json.put("type", this.type.toString());
        json.put("treeType", treeType);
        return json;
    }

    @Override
    public EntityPanel getEntityPanel() {
        return tree_panel;
    }
    
}
