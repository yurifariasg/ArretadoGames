package com.arretadogames.pilot.util;

import android.util.Log;

public class Logger {
	
	public static final boolean LOGGING_ENABLED = true;
	
	public static void e(String component, String message) {
		Log.e(component, message);
	}
	
	public static void e(String message) {
		Logger.e("", message);
	}
	
	public static void v(String message) {
		Logger.e("", message);
	}
	
	public static void v(String component, String message) {
		Log.v(component, message);
	}

}
