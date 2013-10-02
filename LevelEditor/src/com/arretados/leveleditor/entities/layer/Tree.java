package com.arretados.leveleditor.entities.layer;

import com.arretados.leveleditor.DrawMode;
import com.arretados.leveleditor.ResourceManager;
import com.arretados.leveleditor.entities.Entity;
import java.awt.Graphics;
import org.json.simple.JSONObject;


public class Tree extends Entity {
    
    public Tree(int x, int y) {
        super(x, y);
    }

    @Override
    public void drawMyself(Graphics g) {
            g.drawImage(ResourceManager.getImageFor(DrawMode.TREE), (int)(x-(50/2)), (int)(y-(50/2)), (int) (50), (int)(50), null);
    }

    //@Override
    public DrawMode getType() {
        return DrawMode.TREE;
    }

    @Override
    public Entity clone() {
        Tree t = new Tree(x, y);
        return t;
    }

    public void onPropertyChanged(String propertyName, String newValue) {
        
    }

    @Override
    public JSONObject toJSON() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
