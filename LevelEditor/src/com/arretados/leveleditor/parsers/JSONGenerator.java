/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.arretados.leveleditor.parsers;

import com.arretados.leveleditor.entities.Box;
import com.arretados.leveleditor.entities.Fruit;
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
    
    private List<Box> boxes;
    private List<Fruit> fruits;
    private List<int[]> groundLines;

    public JSONGenerator(List<Box> boxes, List<Fruit> fruits, List<int[]> groundLines) {
        this.boxes = boxes;
        this.fruits = fruits;
        this.groundLines = groundLines;
    }
    
    public JSONObject generateJson(){
        
        HashMap<String, JSONArray> hm = createMap();
        return new JSONObject(hm);
    }
    
    public HashMap<String, JSONArray> createMap(){
        final String TYPE_BOX = "box";
        final String TYPE_FRUIT = "fruit";
        
        HashMap<String, JSONArray> hm = new HashMap<String, JSONArray>();
        JSONArray jArray = new JSONArray();
        
        for (int i = 0; i < boxes.size(); i++){
            
            JSONObject jObj = new JSONObject(hm);
            jObj.put("type", TYPE_BOX);
            jObj.put("x", boxes.get(i).getX());
            jObj.put("y", boxes.get(i).getY());
            jObj.put("size", boxes.get(i).getSize());
            
            jArray.add(jObj);
        }
        
        for (int i = 0; i < fruits.size(); i++){
            
            JSONObject jObj = new JSONObject();
            jObj.put("type", TYPE_FRUIT);
            jObj.put("x", fruits.get(i).getX());
            jObj.put("y", fruits.get(i).getY());
            jObj.put("size", fruits.get(i).getSize());
            
            jArray.add(jObj);
        }        
        
        hm.put("entities", jArray);
        
        jArray = new JSONArray();
        
        for (int i = 0; i < groundLines.size(); i++){
            JSONObject jObj = new JSONObject();
            jObj.put("x", groundLines.get(i)[0]);
            jObj.put("y", groundLines.get(i)[1]);
            jArray.add(jObj);
        }
        hm.put("ground", jArray);
        return hm;
        
    }    
}
