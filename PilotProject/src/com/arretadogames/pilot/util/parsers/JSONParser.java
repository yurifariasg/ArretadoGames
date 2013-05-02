package com.arretadogames.pilot.util.parsers;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JSONParser {
	
	public static JSONObject parseJSONString(String jsonStr) throws JSONException{
		JSONObject jsonObject = new JSONObject(jsonStr);

		JSONArray ground = jsonObject.getJSONArray("ground");
		JSONArray entities = jsonObject.getJSONArray("entities");
		
		for (int i=0; i<ground.length(); i++ ){
			JSONObject jsonEntry = ground.getJSONObject(i);
			System.out.println("ground: ("+jsonEntry.getInt("x")+", "+jsonEntry.getInt("y"));
		}

		for (int i=0; i<entities.length(); i++ ){
			JSONObject jsonEntry = ground.getJSONObject(i);
			System.out.println("entity: ("+jsonEntry.getInt("x")+", "+jsonEntry.getInt("y"));
		}
		
		return jsonObject;
	}
	
	public static void roda(){
		
		String json = ""+
		"{"+
			"'ground':"+
			"["+
				"{'y':561,'x':625},"+
				"{'y':563,'x':1234},"+
				"{'y':565,'x':1261}"+
			"],"+
			"'entities':"+
			"["+
				"{'type':'box','y':202,'x':107},"+
				"{'type':'box','y':493,'x':533},"+
				"{'type':'box','y':461,'x':929},"+
				"{'type':'fruit','y':389,'x':328},"+
				"{'type':'fruit','y':420,'x':867},"+
				"{'type':'fruit','y':300,'x':1136}"+
			"]"+
		"}";
		
		try{
			parseJSONString(json);
		}
		catch(JSONException e){
			System.out.println(e);
		}
		
	}
	
}