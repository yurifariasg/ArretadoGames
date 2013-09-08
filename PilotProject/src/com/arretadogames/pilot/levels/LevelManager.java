package com.arretadogames.pilot.levels;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import android.content.res.Resources;
import android.util.Log;

import com.arretadogames.pilot.MainActivity;
import com.arretadogames.pilot.database.GameDatabase;
import com.arretadogames.pilot.util.parsers.LevelParser;


public class LevelManager {
	
	private static List<LevelDescriptor> levels = null;
	
	private static boolean loadLevel(Resources res, LevelDescriptor level) {
		
		try {
			int rawRes = LevelTable.LEVELS[level.getId()];
			BufferedReader br = new BufferedReader(new InputStreamReader(res.openRawResource(rawRes)));
			
			String jsonString = "";
			String line = null;
			while ((line = br.readLine()) != null) {
				jsonString += line;
			}
			return LevelParser.parseJSON(jsonString, level);
		} catch (IOException ioException) {
			Log.e("Level Loading Error", ioException.getMessage());
			return false;
		}
	}
	
	public static int getLevelsCount() {
		return LevelTable.LEVELS.length;
	}
	
	public static boolean loadLevel(LevelDescriptor level) {
		return loadLevel(MainActivity.getContext().getResources(), level);
	}

	public static List<LevelDescriptor> getLevels() {
		if (levels == null)
			loadlevels();
		return levels;
	}

	private static void loadlevels() {
		// Load Levels from DB and place them at levels variable
		levels = GameDatabase.getInstance().getAllLevels();
		System.out.println("!!! ID do level Zero: " + levels.get(0).getId());
		System.out.println("!!! record1: " + levels.get(0).getRecords()[0]);
	}

}
