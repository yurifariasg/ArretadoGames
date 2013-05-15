/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.arretados.leveleditor.parsers;

import com.arretados.leveleditor.Utils;
import com.arretados.leveleditor.entities.Box;
import com.arretados.leveleditor.entities.Fruit;
import com.arretados.leveleditor.entities.Player;
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
    private List<Player> players;

    public JSONGenerator(List<Box> boxes, List<Fruit> fruits, List<int[]> groundLines, List<Player> players) {
        this.boxes = boxes;
        this.fruits = fruits;
        this.groundLines = groundLines;
        this.players = players;
    }
    
    public JSONObject generateJson(){
        
        HashMap<String, JSONArray> hm = createMap();
        return new JSONObject(hm);
    }
    
    public HashMap<String, JSONArray> createMap(){
        final String TYPE_BOX = "box";
        final String TYPE_FRUIT = "fruit";
        final String TYPE_PLAYER = "player";
        
        HashMap<String, JSONArray> hm = new HashMap<String, JSONArray>();
        JSONArray jArrayEntities = new JSONArray();        
        
        for (int i = 0; i < players.size(); i++){
            JSONObject jPlayerObj = new JSONObject();
            jPlayerObj.put("type", TYPE_PLAYER);
            jPlayerObj.put("number", i+1);
            jPlayerObj.put("x", Utils.convertPixelToMeter(players.get(i).getX()));
            jPlayerObj.put("y", Utils.convertPixelToMeter(players.get(i).getY()));
            jArrayEntities.add(jPlayerObj);
        }    
        
        for (int i = 0; i < boxes.size(); i++){
            JSONObject jObj = new JSONObject();
            jObj.put("type", TYPE_BOX);
            jObj.put("x", Utils.convertPixelToMeter(boxes.get(i).getX()));
            jObj.put("y", Utils.convertPixelToMeter(boxes.get(i).getY()));
            jObj.put("size", Utils.convertPixelToMeter(boxes.get(i).getSize()));
            
            jArrayEntities.add(jObj);
        }
        
        for (int i = 0; i < fruits.size(); i++){
            
            JSONObject jObj = new JSONObject();
            jObj.put("type", TYPE_FRUIT);
            jObj.put("x", Utils.convertPixelToMeter(fruits.get(i).getX()));
            jObj.put("y", Utils.convertPixelToMeter(fruits.get(i).getY()));
            jObj.put("size", Utils.convertPixelToMeter(fruits.get(i).getSize()));
            
            jArrayEntities.add(jObj);
        }        
        
        hm.put("entities", jArrayEntities);
        JSONArray jArrayGround = new JSONArray();
        
        for (int i = 0; i < groundLines.size(); i++){
            JSONObject jObj = new JSONObject();
            jObj.put("x", Utils.convertPixelToMeter(groundLines.get(i)[0]) );
            jObj.put("y", Utils.convertPixelToMeter(groundLines.get(i)[1]) );
            jArrayGround.add(jObj);
        }
        hm.put("ground", jArrayGround);
        return hm;
        
    }    
}
