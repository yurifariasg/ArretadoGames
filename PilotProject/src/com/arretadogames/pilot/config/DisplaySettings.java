package com.arretadogames.pilot.config;

public class DisplaySettings {
	
	public static final float TARGET_WIDTH = 800f;
	public static final float TARGET_HEIGHT = 480f;
	public static final float TARGET_FPS = 70f;
	
	public static float DISPLAY_WIDTH = -1;
	public static float DISPLAY_HEIGHT = -1;
	
	public static float WIDTH_RATIO = 1f;
	public static float HEIGHT_RATIO = 1f;
	
	public static final boolean debugViewport = true;
	public static final boolean mockDanilo = false;
	
	public static final boolean USE_OPENGL = true;
	public static final boolean USE_DYNAMIC_TIME = false;
	public static final boolean PROFILE_SPEED = false;
	public static final boolean PROFILE_RENDER_SPEED = false;
	public static final boolean PROFILE_GAME_CAMERA = false;
	
	public static final int GROUND_ENTITY_THRESHOLD = 3;
	public static final boolean DRAW_DEBUG_GROUND = true;

	public static final boolean SHOW_FPS = true;
	public static final int FPS_AVG_BUFFER_SIZE = 10;
}
