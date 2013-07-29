package com.arretadogames.pilot.util.parsers;

import java.util.ArrayList;
import java.util.List;

import org.jbox2d.common.Vec2;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.arretadogames.pilot.entities.EntityType;
import com.arretadogames.pilot.entities.PlayerNumber;
import com.arretadogames.pilot.levels.EntityDescriptor;
import com.arretadogames.pilot.levels.GroundDescriptor;
import com.arretadogames.pilot.levels.LevelDescriptor;
import com.arretadogames.pilot.levels.PlayerDescriptor;

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
				if (EntityType.BOX.toString().equals(entityType)) {
					// is it a box ?
					entity = new EntityDescriptor((float) jsonEntity.getDouble("x"),
							(float) jsonEntity.getDouble("y"),
							EntityType.BOX, (float) jsonEntity.getDouble("size"));
				} else if (EntityType.FRUIT.toString().equals(entityType)) {
					// is it a fruit ?
					entity = new EntityDescriptor((float) jsonEntity.getDouble("x"),
							(float) jsonEntity.getDouble("y"),
							EntityType.FRUIT, (float) jsonEntity.getDouble("size"));
				} else if (EntityType.FINALFLAG.toString().equals(entityType)) {
					// is it a fruit ?
					entity = new EntityDescriptor((float) jsonEntity.getDouble("x"),
							(float) jsonEntity.getDouble("y"),
							EntityType.FINALFLAG);
				} else if (EntityType.COIN.toString().equals(entityType)) {
					entity = new EntityDescriptor((float) jsonEntity.getDouble("x"),
							(float) jsonEntity.getDouble("y"),
							EntityType.COIN);
				} else if (EntityType.PLAYER.toString().equals(entityType)) {
					// is it a player ?
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
					
				} else {
					// Entity not defined
					Log.e("LevelDescriptor.jsonParse()", "Entity " + entityType + " not defined");
					continue;
				}
				
				entities.add(entity);
			}
			
			GroundDescriptor groundDescriptor = new GroundDescriptor();
			JSONArray groundArray = master.getJSONArray("ground");
			for (int i = 0; i < groundArray.length() ; i++) {
				JSONObject jsonGroundPoint = groundArray.getJSONObject(i);
				groundDescriptor.addPoint(new Vec2(
						(float) jsonGroundPoint.getDouble("x"),
						(float) jsonGroundPoint.getDouble("y")));
			}
			
			// Set Level Data
			level.setData(entities, groundDescriptor);
			return true;
		} catch (JSONException ex) {
			ex.printStackTrace();
		}
		
		return false;
	}
}
