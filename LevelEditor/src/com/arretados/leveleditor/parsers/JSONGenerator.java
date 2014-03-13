/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.arretados.leveleditor.parsers;

import com.arretados.leveleditor.Utils;
import com.arretados.leveleditor.entities.Entity;
import com.arretados.leveleditor.entities.Flag;
import java.util.HashMap;
import java.util.List;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 *
 * @author Bruno
 */
public class JSONGenerator {
    
    private List<Entity> entities;
    private Flag flag;
    private final int groundHeight;
    private final int totalHeight;
    private final int totalWidth;

    public JSONGenerator(List<Entity> entities, int groundHeight, int totalHeight,
            int totalWidth, Flag flag) {
        this.entities = entities;
        this.groundHeight = groundHeight;
        this.totalHeight = totalHeight;
        this.totalWidth = totalWidth;
        this.flag = flag;
    }
    
    public JSONObject generateJson(){
        HashMap<String, JSONArray> hm = new HashMap<String, JSONArray>();
        JSONArray jArrayEntities = new JSONArray();
        
        
        for (int i = 0 ; i < entities.size() ; i++) {
            jArrayEntities.add(entities.get(i).toJSON());
        }
        
        jArrayEntities.add(flag.toJSON());
        
        
        float yOffset = Utils.convertPixelToMeter(totalHeight - groundHeight);
        
        for (int i = 0 ; i < jArrayEntities.size() ; i++) {
            // Update Y based on ground
            float previousY = (Float) ((JSONObject)jArrayEntities.get(i)).get("y");
            ((JSONObject)jArrayEntities.get(i)).put("y", yOffset - previousY);
        }
        
        hm.put("entities", jArrayEntities);
        JSONObject object = new JSONObject(hm);
        object.put("height", totalHeight);
        object.put("width", totalWidth);
        
        return object;
        
    }    
}
