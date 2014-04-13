/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.arretados.leveleditor;

import com.arretados.leveleditor.entities.Box;
import com.arretados.leveleditor.entities.BoxItem;
import com.arretados.leveleditor.entities.Breakable;
import com.arretados.leveleditor.entities.Coin;
import com.arretados.leveleditor.entities.Flag;
import com.arretados.leveleditor.entities.Fluid;
import com.arretados.leveleditor.entities.Hole;
import com.arretados.leveleditor.entities.Liana;
import com.arretados.leveleditor.entities.OneWayWall;
import com.arretados.leveleditor.entities.Player;
import com.arretados.leveleditor.entities.Pulley;
import com.arretados.leveleditor.entities.Spike;
import com.arretados.leveleditor.entities.layer.Grass;
import com.arretados.leveleditor.entities.layer.Shrub;
import com.arretados.leveleditor.entities.layer.Tree;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 *
 * @author Bruno
 */
public class LevelLoader {
    
    private JSONObject jsonLevel;
    private GameCanvas gc;
    private LevelEditorView editorView;

    LevelLoader(JSONObject json, GameCanvas level, LevelEditorView levelEditor) {
        this.jsonLevel = json;
        this.gc = level;
        this.editorView = levelEditor;
    }
    
    public void parseJson(){
        gc.clearObjectsList();
        int groundHeight = gc.getGroundHeight();
        
        long jsonTotalHeight = (Long) jsonLevel.get("height");
        long jsonTotalWidth = (Long) jsonLevel.get("width");
        editorView.setCanvasDimensions(jsonTotalWidth, jsonTotalHeight);
        float yOffset = Utils.convertPixelToMeter(jsonTotalHeight - groundHeight);
        
        JSONArray jArray = (JSONArray) jsonLevel.get("entities");
        JSONObject jObj;
        for (int i = 0; i < jArray.size(); i++){
            jObj = (JSONObject) jArray.get(i);
            
            jObj.put("y", yOffset - (Double)jObj.get("y"));
            
            if ( String.valueOf(jObj.get("type")).equals(DrawMode.BOX.toString()) ){
                
                //BOX
                gc.addEntities(new Box(jObj));
                
            }else if (String.valueOf(jObj.get("type")).equals(DrawMode.BREAKABLE.toString())){
                
                //BREAKABLE
                gc.addEntities(new Breakable(jObj));
                
            }else if (String.valueOf(jObj.get("type")).equals(DrawMode.COIN.toString())){
                
                //COIN
                gc.addEntities(new Coin(jObj));
                
            }else if (String.valueOf(jObj.get("type")).equals(DrawMode.FLAG.toString())){
                
                //FLAG
                gc.setFlag(new Flag(jObj));
                
            }else if (String.valueOf(jObj.get("type")).equals(DrawMode.FLUID.toString())){
                
                //FLUID
                gc.addEntities(new Fluid(jObj));
                
            }else if (String.valueOf(jObj.get("type")).equals(DrawMode.LIANA.toString())){
                
                //LIANA
                gc.addEntities(new Liana(jObj));
                
            }else if (String.valueOf(jObj.get("type")).equals(DrawMode.ONEWAY_WALL.toString())){
                
                //ONEWAY_WALL
                gc.addEntities(new OneWayWall(jObj));
                
            }else if (String.valueOf(jObj.get("type")).equals(DrawMode.PLAYER.toString())){
                
                //PLAYER
                gc.addEntities(new Player(jObj));
                
            }else if (String.valueOf(jObj.get("type")).equals(DrawMode.PULLEY.toString())){
                
                //PULLEY
                gc.addEntities(new Pulley(jObj));
                
            }else if (String.valueOf(jObj.get("type")).equals(DrawMode.GRASS.toString())){
                
                //GRASS
                gc.addEntities(new Grass(jObj));
                
            }else if (String.valueOf(jObj.get("type")).equals(DrawMode.SHRUB.toString())){
                
                //SHRUB
                gc.addEntities(new Shrub(jObj));
                
            }else if (String.valueOf(jObj.get("type")).equals(DrawMode.SPIKE.toString())){
                
                //SHRUB
                gc.addEntities(new Spike(jObj));
                
            }else if (String.valueOf(jObj.get("type")).equals(DrawMode.TREE.toString())){

                //TREE
                gc.addEntities(new Tree(jObj));
                
            }else if (String.valueOf(jObj.get("type")).equals(DrawMode.HOLE.toString())) {
                
                // HOLE
                gc.addEntities(new Hole(jObj));
                
            }else if (String.valueOf(jObj.get("type")).equals(DrawMode.BOX_ITEM.toString())) {
                
                // BOX ITEM
                gc.addEntities(new BoxItem(jObj));
                
            } else {
                System.out.println("Not Found: " + jObj.get("type"));
            }
            
        }
        
    }
    
}
