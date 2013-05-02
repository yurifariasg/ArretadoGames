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
					"{'type':'box','y':202,'x':107},"+
					"{'type':'box','y':493,'x':533},"+
					"{'type':'box','y':461,'x':929},"+
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
				int x = ent.getInt("x");
				int y = ent.getInt("y");
				//int size = ent.getInt("size");
				EntityType t = EntityType.valueOf(type);
				if( t == EntityType.BOX){
					Entity entity = new Box(x, y);
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
