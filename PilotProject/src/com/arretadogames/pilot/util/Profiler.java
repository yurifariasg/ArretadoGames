package com.arretadogames.pilot.util;

import com.arretadogames.pilot.config.GameSettings;

import android.util.Log;

public class Profiler {
	
	public enum ProfileType {
		STEP, RENDER, BASIC
	}
	
	private static long time;
	
	public static void initTick(ProfileType profileType) {
		if (isProfileTypeActive(profileType))
			time = System.nanoTime();
	}
	
	public static void profileFromLastTick(ProfileType profileType, String component) {
		if (isProfileTypeActive(profileType))
			Log.d("Profiler: " + component, "Took " + (System.nanoTime() - time) + " nanoseconds");
	}

	private static boolean isProfileTypeActive(ProfileType profileType) {
		switch (profileType) {
		case BASIC:
			return GameSettings.PROFILE_SPEED;
		case RENDER:
			return GameSettings.PROFILE_RENDER_SPEED;
		case STEP:
			return GameSettings.PROFILE_STEP_SPEED;
		}
		
		return false;
	}
	
	

}
