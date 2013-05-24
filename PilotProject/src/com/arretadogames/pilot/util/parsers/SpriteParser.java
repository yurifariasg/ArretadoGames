package com.arretadogames.pilot.util.parsers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

import android.util.Log;

public class SpriteParser {
	private static final String ID_SEPARATOR = ":";
	private static final String PROPERTY_SEPARATOR = ",";
	
	/*
	 * Methods to parse Image File
	 */
	public static HashMap<String, String[]> parseSpriteFile(InputStream in) throws IOException {
		
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		String line;
		int lineCounter = 1;
		HashMap<String, String[]> fileContents = new HashMap<String, String[]>();
		
		while ((line = br.readLine()) != null) {
			if (line != "") {
				// Parse the Line and Add the RawSpriteState
				line = removeComments(line);
				if (line.trim() == "")
					continue;
				
				String[] idSplit = line.split(ID_SEPARATOR);
				if (idSplit.length < 2) {
					Log.e("Parse Image File", "Malformed Line at line " + lineCounter);
					continue;
				}
				
				String id = idSplit[0].trim();
				String[] properties = idSplit[1].split(PROPERTY_SEPARATOR);
				for (int i = 0 ; i < properties.length ; i++)
					properties[i] = properties[i].trim();
					
				fileContents.put(id, properties);
			}
			lineCounter++;
		}
		
		return fileContents;
	}
	
	private static String removeComments(String line) {
		int index = line.indexOf('#');
		if (index != -1) {
			return line.substring(0,  index);
		}
		return line;
	}
	
	/*
	 * Methods to parse Animations File
	 */
	
	public static HashMap<String, RawSpriteState> parseAnimationFile(InputStream in) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		
		HashMap<String, RawSpriteState> RawSpriteStates = new HashMap<String, RawSpriteState>();
		String line;
		while ((line = br.readLine()) != null) {
			if (line != "") {
				// Parse the Line and Add the RawSpriteState
				RawSpriteState kf = parseAnimationLine(line);
				if (kf != null) {
					RawSpriteStates.put(kf.getName(), kf);
				}
			}
		}
		return RawSpriteStates;
	}
	
	private static RawSpriteState parseAnimationLine(String line) {
		RawSpriteState ad = null;
		try {
			String[] parts = line.split(ID_SEPARATOR);
			String id = parts[0];
			// Create the RawSpriteState
			ad = new RawSpriteState(id, parts.length - 1);
			
			for (int i = 1 ; i < parts.length ; i ++) { // Get Each KeyFrame
				String[] keyFrameDesc = parts[i].split(PROPERTY_SEPARATOR);
				
				String keyFrame = keyFrameDesc[0];
				float keyFrameDuration = Long.parseLong(keyFrameDesc[1]) / 1000f;
				
				ad.addKeyFrame(keyFrame, keyFrameDuration);
			}
		
		} catch (Exception e) {
			Log.e("Parsing Animation Line Error", e.getLocalizedMessage());
		}
		return ad;
	}
	
	/*
	 * Methods to parse Animations Type File
	 */
	/*
	public static HashMap<GameEntityType, String[]> parseAnimationsTypeFile(InputStream in) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		
		HashMap<GameEntityType, String[]> gameEntityAnimations = new HashMap<GameEntityType, String[]>();
		String line;
		while ((line = br.readLine()) != null) {
			if (line != "") {
				// Parse the Line and get GameEntityType
				
				String[] typeSeparated = line.split(ID_SEPARATOR);
				
				GameEntityType entityType = GameEntityType.get(typeSeparated[0]);
				if (entityType == null) { // Check if GameEntity is Valid
					Log.e("AnimationsType Parsing", "No GameEntityType for " + typeSeparated[0]);
					continue;
				}
				
				// Get Animations and Add to HashMap
				String[] animations = typeSeparated[1].split(PROPERTY_SEPARATOR);
				gameEntityAnimations.put(entityType, animations);
				
			}
		}
		return gameEntityAnimations;
	}*/

}
