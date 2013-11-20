/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.arretados.leveleditor;

import com.arretados.leveleditor.entities.Box;
import com.arretados.leveleditor.entities.Breakable;
import com.arretados.leveleditor.entities.Coin;
import com.arretados.leveleditor.entities.Flag;
import com.arretados.leveleditor.entities.Fluid;
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
    
    public LevelLoader(JSONObject level, GameCanvas gc){
        this.jsonLevel = level;
        this.gc = gc;
    }
    
    public void parseJson(){
        
        JSONArray jArray = (JSONArray) jsonLevel.get("entities");
        JSONObject jObj;
        for (int i = 0; i < jArray.size(); i++){
            jObj = (JSONObject) jArray.get(i);
            if ( String.valueOf(jObj.get("type")).equals(DrawMode.BOX.toString()) ){
                
                //BOX
                System.out.println("BOX_in");
                gc.addEntities(new Box(jObj));
                System.out.println("BOX_out");
                
            }else if (String.valueOf(jObj.get("type")).equals(DrawMode.BREAKABLE.toString())){
                
                //BREAKABLE
                System.out.println("BREAKABLE_in");
                gc.addEntities(new Breakable(jObj));
                System.out.println("BREAKABLE_out");
                
            }else if (String.valueOf(jObj.get("type")).equals(DrawMode.COIN.toString())){
                
                //COIN
                System.out.println("COIN_in");
                gc.addEntities(new Coin(jObj));
                System.out.println("COIN_out");
                
            }else if (String.valueOf(jObj.get("type")).equals(DrawMode.FLAG.toString())){
                
                //FLAG
                System.out.println("FLAG_in");
                gc.addEntities(new Flag(jObj));
                System.out.println("FLAG_out");
                
            }else if (String.valueOf(jObj.get("type")).equals(DrawMode.FLUID.toString())){
                
                //FLUID
                System.out.println("FLUID_in");
                gc.addEntities(new Fluid(jObj));
                System.out.println("FLUID_out");
                
            }else if (String.valueOf(jObj.get("type")).equals(DrawMode.LIANA.toString())){
                
                //LIANA
                System.out.println("LIANA_in");
                gc.addEntities(new Liana(jObj));
                System.out.println("LIANA_out");
                
            }else if (String.valueOf(jObj.get("type")).equals(DrawMode.ONEWAY_WALL.toString())){
                
                //ONEWAY_WALL
                System.out.println("ONEWAY_in");
                gc.addEntities(new OneWayWall(jObj));
                System.out.println("ONEWAY_out");
                
            }else if (String.valueOf(jObj.get("type")).equals(DrawMode.PLAYER.toString())){
                
                //PLAYER
                System.out.println("PLAYER_in");
                gc.addEntities(new Player(jObj));
                System.out.println("PLAYER_out");
                
            }else if (String.valueOf(jObj.get("type")).equals(DrawMode.PULLEY.toString())){
                
                //PULLEY
                System.out.println("PULLEY_in");
                gc.addEntities(new Pulley(jObj));
                System.out.println("PULLEY_out");
                
            }else if (String.valueOf(jObj.get("type")).equals(DrawMode.GRASS)){
                
                //GRASS
                System.out.println("GRASS_in");
                gc.addEntities(new Grass(jObj));
                System.out.println("GRASS_out");
                
            }else if (String.valueOf(jObj.get("type")).equals(DrawMode.SHRUB)){
                
                //SHRUB
                System.out.println("SHRUB_in");
                gc.addEntities(new Shrub(jObj));
                System.out.println("SHRUB_out");
                
            }else if (String.valueOf(jObj.get("type")).equals(DrawMode.SPIKE)){
                
                //SHRUB
                System.out.println("SPIKE_in");
                gc.addEntities(new Spike(jObj));
                System.out.println("SPIKE_out");
                
            }else if (String.valueOf(jObj.get("type")).equals(DrawMode.TREE)){

                //TREE
                System.out.println("PULLEY_in");
                gc.addEntities(new Tree(jObj));
                System.out.println("PULLEY_out");
                
            }
            
        }
        
    }
    
}
