package com.arretadogames.pilot.levels;

import java.util.ArrayList;
import java.util.List;

import org.jbox2d.common.Vec2;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.arretadogames.pilot.entities.EntityType;
import com.arretadogames.pilot.entities.PlayerNumber;

public class LevelDescriptor {
	
	private List<EntityDescriptor> entities;
	private GroundDescriptor groundDescriptor;
	
	private LevelDescriptor(List<EntityDescriptor> entities, GroundDescriptor groundDescriptor) { // Disable public creation
		this.entities = entities;
		this.groundDescriptor = groundDescriptor;
	}
	
	public GroundDescriptor getGroundDescriptor() {
		return groundDescriptor;
	}
	
	public List<EntityDescriptor> getEntities() {
		return entities;
	}

	public static LevelDescriptor parseJSON(String jsonString) {
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
							10 - (float) jsonEntity.getDouble("y"),
							EntityType.BOX, (float) jsonEntity.getDouble("size"));
				} else if (EntityType.FRUIT.toString().equals(entityType)) {
					// is it a fruit ?
					entity = new EntityDescriptor((float) jsonEntity.getDouble("x"),
							10 - (float) jsonEntity.getDouble("y"),
							EntityType.FRUIT, (float) jsonEntity.getDouble("size"));
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
							10 - (float) jsonEntity.getDouble("y"),
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
						10 - (float) jsonGroundPoint.getDouble("y")));
			}
			
			return new LevelDescriptor(entities, groundDescriptor);
		} catch (JSONException ex) {
			ex.printStackTrace();
		}
		
		return null;
	}

}
