package com.arretadogames.pilot.levels;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import com.arretadogames.pilot.GameActivity;
import com.arretadogames.pilot.R;
import android.content.res.Resources;


public class LevelManager {
	
	// List of Levels
	private static final int[] LEVELS_RESOURCES = {R.raw.basic_level, R.raw.second_level,
		R.raw.first_stage, R.raw.second_stage, R.raw.third_stage};
	
	private static LevelDescriptor loadLevel(Resources res, int levelResource) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(res.openRawResource(levelResource)));
		
		String jsonString = "";
		String line = null;
		while ((line = br.readLine()) != null) {
			jsonString += line;
		}
		return LevelDescriptor.parseJSON(jsonString);
	}
	
	public static int getLevelsCount() {
		return LEVELS_RESOURCES.length;
	}
	
	public static LevelDescriptor loadLevel(int index) throws IOException {
		return loadLevel(GameActivity.getContext().getResources(), LEVELS_RESOURCES[index]);
	}

}
