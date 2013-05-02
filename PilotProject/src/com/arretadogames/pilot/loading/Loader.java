package com.arretadogames.pilot.loading;

import java.util.ArrayList;
import java.util.Collection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.arretadogames.pilot.entities.Box;
import com.arretadogames.pilot.entities.Entity;
import com.arretadogames.pilot.entities.EntityType;
import com.arretadogames.pilot.entities.Fruit;

public class Loader {

	public final String json;
	public static String jsonExample = ""+
			"{"+
				"'entities':"+
				"["+
					"{'type':'box','y':10,'x':10},"+
					"{'type':'box','y':15,'x':14},"+
					"{'type':'box','y':100,'x':10},"+
				"]"+
			"}";
	public Loader(String json){
		this.json = json;
	}
	
	public Collection<Entity> getEntities(){
		Collection<Entity> col = new ArrayList<Entity>();
		try{
			JSONObject jsonObject = new JSONObject(json);
			JSONArray entities = jsonObject.getJSONArray("entities");
			
			for( int i = 0 ; i < entities.length(); i++){
				JSONObject ent = entities.getJSONObject(i);
				String type = ent.getString("type");
				type = type.toUpperCase();
				int x = ent.getInt("x");
				int y = ent.getInt("y");
				//int size = ent.getInt("size");
				EntityType t = EntityType.valueOf(type);
				if( t == EntityType.BOX){
					Entity entity = new Box(x, y,10);
					col.add(entity);
				}
				else{
					Entity entity = new Fruit(x, y);
					col.add(entity);
				}
			}
		}catch(JSONException e){
			System.out.println("Error: "+e.getMessage());
		}
		return col;
		
	}
}
