package com.arretadogames.pilot.loading;

import java.util.ArrayList;
import java.util.Collection;

import org.jbox2d.collision.shapes.ChainShape;
import org.jbox2d.common.Vec2;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.arretadogames.pilot.entities.Box;
import com.arretadogames.pilot.entities.Entity;
import com.arretadogames.pilot.entities.EntityType;
import com.arretadogames.pilot.entities.Fruit;
import com.arretadogames.pilot.entities.Ground;

public class Loader {

	public final String json;
	public static String jsonExample = ""+
			"{"+
				"'entities':"+
				"["+
					"{'type':'box','y':10,'x':10, size : 2},"+
					"{'type':'box','y':15,'x':14, size : 2},"+
					"{'type':'box','y':100,'x':10 , size : 2},"+
				"]"+
			"}";
	public static String jsonExample2 = "{\"ground\":[{\"y\":445,\"x\":222},{\"y\":486,\"x\":449}, {\"y\":528,\"x\":850}, {\"y\":536,\"x\":1143}, {\"y\":530,\"x\":1318},{\"y\":527,\"x\":1439},],\"entities\":[{\"type\":\"box\",\"y\":4.58,\"size\":1.0,\"x\":5.45},{\"type\":\"box\",\"y\":20.65,\"size\":1.0,\"x\":8.64},{\"type\":\"box\",\"y\":4.74,\"size\":1.0,\"x\":10.17},{\"type\":\"box\",\"y\":4.17,\"size\":1.0,\"x\":1.09},{\"type\":\"fruit\",\"y\":5.87,\"size\":0.25,\"x\":9.53},]}";
	
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
				EntityType t = EntityType.valueOf(type);
				if( t == EntityType.BOX){
					float size = (float)ent.getDouble("size");
					Entity entity = new Box(x, y,size);
					col.add(entity);
				}
				else{
					float size = (float) ent.getDouble("size");
					Entity entity = new Fruit(x, y, size);
					col.add(entity);
				}
			}
			
			JSONArray ground = jsonObject.getJSONArray("ground");
			Vec2 vec[] = new Vec2[ground.length()];
			for( int i = 0 ; i < ground.length(); i++){
				JSONObject ent = entities.getJSONObject(i);
				int x = ent.getInt("x");
				int y = ent.getInt("y");
				vec[i] = new Vec2(x,y);
			}
			col.add(new Ground(vec,ground.length()));
		}catch(JSONException e){
			System.out.println("Error: "+e.getMessage());
		}
		return col;
		
	}
}
