package com.arretadogames.pilot.levels;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import android.content.res.Resources;


public class LevelManager {
	
	private static final int[] LEVELS_RESOURCES = {1};
	
	public static LevelDescriptor loadLevel(Resources res, int levelResource) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(res.openRawResource(levelResource)));
		
		String jsonString = "";
		String line = null;
		while ((line = br.readLine()) != null) {
			jsonString += line;
		}
		return LevelDescriptor.parseJSON(jsonString);
	}

}
