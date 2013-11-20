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
    
    JSONObject j;
    
    private List<Entity> entities;
    private Flag flag;
    private final int groundHeight;
    private final int totalHeight;

    public JSONGenerator(List<Entity> entities, int groundHeight, int totalHeight,
            Flag flag) {
        this.entities = entities;
        this.groundHeight = groundHeight;
        this.totalHeight = totalHeight;
        this.flag = flag;
    }
    
    public JSONObject generateJson(){
        HashMap<String, JSONArray> hm = createMap();
        return new JSONObject(hm);
    }
    
    public HashMap<String, JSONArray> createMap(){
//        final String TYPE_BOX = "box";
//        final String TYPE_FRUIT = "fruit";
//        final String TYPE_COIN = "coin";
//        final String TYPE_ONEWAY_WALL = "oneway_wall";
//        final String TYPE_PULLEY = "pulley";
//        final String TYPE_FLUID = "fluid";
//        final String TYPE_BREAKABLE = "breakable";
//        final String TYPE_LIANA = "liana";
//        final String TYPE_PLAYER = "player";
//        final String TYPE_FLAG = "finalflag";
//        final String TYPE_SPIKE = "spike";
        
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
        
        /*
        for (int i = 0; i < boxes.size(); i++){
            JSONObject jObj = new JSONObject();
            jObj.put("type", TYPE_BOX);
            jObj.put("x", Utils.convertPixelToMeter(boxes.get(i).getX()));
            jObj.put("y", 10-Utils.convertPixelToMeter(boxes.get(i).getY()));
            jObj.put("size", Utils.convertPixelToMeter(boxes.get(i).getSize()));
            
            jArrayEntities.add(jObj);
        }
        
        for (int i = 0; i < fruits.size(); i++){
            JSONObject jObj = new JSONObject();
            jObj.put("type", TYPE_FRUIT);
            jObj.put("x", Utils.convertPixelToMeter(fruits.get(i).getX()));
            jObj.put("y", 10-Utils.convertPixelToMeter(fruits.get(i).getY()));
            jObj.put("size", Utils.convertPixelToMeter(fruits.get(i).getSize()));
            
            jArrayEntities.add(jObj);
        }
        
        for (int i = 0; i < coins.size(); i++){
            
            JSONObject jObj = new JSONObject();
            jObj.put("type", TYPE_COIN);
            jObj.put("x", Utils.convertPixelToMeter(coins.get(i).getX()));
            jObj.put("y", 10-Utils.convertPixelToMeter(coins.get(i).getY()));
            jObj.put("size", Utils.convertPixelToMeter(coins.get(i).getSize()));
            
            jArrayEntities.add(jObj);
        }
        
        for (int i = 0; i < oneWays.size(); i++){
            
            JSONObject jObj = new JSONObject();
            jObj.put("type", TYPE_ONEWAY_WALL);
            jObj.put("x", Utils.convertPixelToMeter(oneWays.get(i).getX()));
            jObj.put("y", 10-Utils.convertPixelToMeter(oneWays.get(i).getY()));
            jObj.put("size", Utils.convertPixelToMeter(oneWays.get(i).getSize()));
            
            jArrayEntities.add(jObj);
        }
        
        for (int i = 0; i < pulleys.size(); i++){
            
            JSONObject jObj = new JSONObject();
            jObj.put("type", TYPE_PULLEY);
            jObj.put("x", Utils.convertPixelToMeter(pulleys.get(i).getX()));
            jObj.put("y", 10-Utils.convertPixelToMeter(pulleys.get(i).getY()));
            jObj.put("size", Utils.convertPixelToMeter(pulleys.get(i).getSize()));
            
            jArrayEntities.add(jObj);
        }
        
        for (int i = 0; i < fluids.size(); i++){
            
            JSONObject jObj = new JSONObject();
            jObj.put("type", TYPE_FLUID);
            jObj.put("x", Utils.convertPixelToMeter(fluids.get(i).getX()));
            jObj.put("y", 10-Utils.convertPixelToMeter(fluids.get(i).getY()));
            jObj.put("size", Utils.convertPixelToMeter(fluids.get(i).getSize()));
            
            jArrayEntities.add(jObj);
        }
        
        for (int i = 0; i < breakables.size(); i++){
            
            JSONObject jObj = new JSONObject();
            jObj.put("type", TYPE_BREAKABLE);
            jObj.put("x", Utils.convertPixelToMeter(breakables.get(i).getX()));
            jObj.put("y", 10-Utils.convertPixelToMeter(breakables.get(i).getY()));
            
            jArrayEntities.add(jObj);
        }
        
        for (int i = 0; i < liana.size(); i++){
            JSONObject jObj = new JSONObject();
            jObj.put("type", TYPE_LIANA);
            jObj.put("x0", Utils.convertPixelToMeter(liana.get(i).getX0()));
            jObj.put("y0", 10-Utils.convertPixelToMeter(liana.get(i).getY0()));
            jObj.put("x1", Utils.convertPixelToMeter(liana.get(i).getX1()));
            jObj.put("y1", 10-Utils.convertPixelToMeter(liana.get(i).getY1()));
            jObj.put("size", Utils.convertPixelToMeter(liana.get(i).getSize()));
            
            jArrayEntities.add(jObj);
        }*/
        
//        JSONObject jObjFlag = new JSONObject();
//        jObjFlag.put("type", TYPE_FLAG);
//        jObjFlag.put("x", Utils.convertPixelToMeter(flag.getX()));
//        jObjFlag.put("y", 10-Utils.convertPixelToMeter(flag.getY()));
//        jArrayEntities.add(jObjFlag);
        
        hm.put("entities", jArrayEntities);
//        JSONArray jArrayGround = new JSONArray();
        
//        for (int i = 0; i < groundLines.size(); i++){
//            JSONObject jObj = new JSONObject();
//            jObj.put("x", Utils.convertPixelToMeter(groundLines.get(i)[0]) );
//            jObj.put("y", 10-Utils.convertPixelToMeter(groundLines.get(i)[1]) );
//            jArrayGround.add(jObj);
//        }
//        hm.put("ground", jArrayGround);
        return hm;
        
    }    
}
