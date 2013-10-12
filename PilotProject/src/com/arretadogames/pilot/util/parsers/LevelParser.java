package com.arretadogames.pilot.util.parsers;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.arretadogames.pilot.entities.EntityType;
import com.arretadogames.pilot.entities.PlayerNumber;
import com.arretadogames.pilot.levels.EntityDescriptor;
import com.arretadogames.pilot.levels.GroundDescriptor;
import com.arretadogames.pilot.levels.LevelDescriptor;
import com.arretadogames.pilot.levels.LianaDescriptor;
import com.arretadogames.pilot.levels.PlayerDescriptor;
import com.arretadogames.pilot.levels.WaterDescriptor;

public class LevelParser {
	public static boolean parseJSON(String jsonString, LevelDescriptor level) {
		try {
			JSONObject master = new JSONObject(jsonString);
			JSONArray entitiesJsonArray = master.getJSONArray("entities");
			List<EntityDescriptor> entities = new ArrayList<EntityDescriptor>();
			
			for (int i = 0 ; i < entitiesJsonArray.length(); i++) {
				JSONObject jsonEntity = entitiesJsonArray.getJSONObject(i);
				
				// Convert jsonEntity to EntityDescriptor
				String entityType = jsonEntity.getString("type");
				EntityDescriptor entity;
				
				if (EntityType.BOX.toString().equals(entityType)) { // BOX
					
					entity = new EntityDescriptor((float) jsonEntity.getDouble("x"),
							(float) jsonEntity.getDouble("y"),
							EntityType.BOX, (float) jsonEntity.getDouble("size"));
					
				} else if (EntityType.COIN.toString().equals(entityType)) { // COIN
					
					entity = new EntityDescriptor((float) jsonEntity.getDouble("x"),
							(float) jsonEntity.getDouble("y"),
							EntityType.COIN);
					
				} else if (EntityType.FRUIT.toString().equals(entityType)) { // FRUIT
					
					entity = new EntityDescriptor((float) jsonEntity.getDouble("x"),
							(float) jsonEntity.getDouble("y"),
							EntityType.FRUIT, (float) jsonEntity.getDouble("size"));
					
				} else if (EntityType.PLAYER.toString().equals(entityType)) { // PLAYER
					PlayerNumber pNumber;
					int pNumberInt = jsonEntity.getInt("number");
					switch (pNumberInt) {
					case 1:
						pNumber = PlayerNumber.ONE;
						break;
					case 2:
						pNumber = PlayerNumber.TWO;
						break;
					default:
						Log.e("LevelDescriptor.jsonParse()", "PlayerNumber " + pNumberInt + " not defined");
						continue;
					}
					
					entity = new PlayerDescriptor((float) jsonEntity.getDouble("x"),
							(float) jsonEntity.getDouble("y"),
							EntityType.PLAYER, pNumber);
					
				} else if (EntityType.ONEWAY_WALL.toString().equals(entityType)){ // ONEWAY_WALL
					
					entity = new EntityDescriptor((float) jsonEntity.getDouble("x"),
							(float) jsonEntity.getDouble("y"),
							EntityType.ONEWAY_WALL);
				
				} else if (EntityType.PULLEY.toString().equals(entityType)) { // PULLEY

					entity = new EntityDescriptor((float) jsonEntity.getDouble("x"),
							(float) jsonEntity.getDouble("y"),
							EntityType.PULLEY);

//TODO					
/*				} else if (EntityType.FLUID.toString().equals(entityType)) { // FLUID

					entity = new EntityDescriptor((float) jsonEntity.getDouble("x"),
							(float) jsonEntity.getDouble("y"),
							EntityType.FLUID);*/
				} else if (EntityType.TREE.toString().equals(entityType)) {
					
					entity = new EntityDescriptor((float) jsonEntity.getDouble("x"),
							(float) jsonEntity.getDouble("y"),
							EntityType.TREE);
					
				} else if (EntityType.SHRUB.toString().equals(entityType)) {
					
					entity = new EntityDescriptor((float) jsonEntity.getDouble("x"),
							(float) jsonEntity.getDouble("y"),
							EntityType.SHRUB);
					
				} else if (EntityType.BREAKABLE.toString().equals(entityType)) { // BREAKABLE

					entity = new EntityDescriptor((float) jsonEntity.getDouble("x"),
							(float) jsonEntity.getDouble("y"),
							EntityType.BREAKABLE);
					
				} else if (EntityType.LIANA.toString().equals(entityType)) { // LIANA
					
					entity = new LianaDescriptor(
							(float) jsonEntity.getDouble("x0"),
							(float) jsonEntity.getDouble("y0"),
							(float) jsonEntity.getDouble("x1"),
							(float) jsonEntity.getDouble("y1"),
							(float) jsonEntity.getDouble("size"),
							EntityType.LIANA);

					
				} else if (EntityType.FINALFLAG.toString().equals(entityType)) { // FINAL FLAG
					
					entity = new EntityDescriptor((float) jsonEntity.getDouble("x"),
							(float) jsonEntity.getDouble("y"),
							EntityType.FINALFLAG);
				}else if(EntityType.WATER.toString().equals(entityType)){
					entity = new WaterDescriptor((float) jsonEntity.getDouble("x"),
							(float) jsonEntity.getDouble("y"),
							EntityType.WATER, (float) jsonEntity.getDouble("width"), (float) jsonEntity.getDouble("height"),  (float) jsonEntity.getDouble("density"));
				} else {		// Entity not defined
					Log.e("LevelDescriptor.jsonParse()", "Entity " + entityType + " not defined");
					continue;
				}
				
				entities.add(entity);
			}
			
			GroundDescriptor groundDescriptor = new GroundDescriptor();
//			JSONArray groundArray = master.getJSONArray("ground");
//			for (int i = 0; i < groundArray.length() ; i++) {
//				JSONObject jsonGroundPoint = groundArray.getJSONObject(i);
//				groundDescriptor.addPoint(new Vec2(
//						(float) jsonGroundPoint.getDouble("x"),
//						(float) jsonGroundPoint.getDouble("y")));
//			}
//			
//			// Set Level Data
			level.setData(entities, groundDescriptor);
			return true;
		} catch (JSONException ex) {
			ex.printStackTrace();
		}
		
		return false;
	}
}
