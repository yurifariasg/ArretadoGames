package com.arretadogames.pilot.config;

public class GameSettings {
	
	public static final float TARGET_WIDTH = 800f;
	public static final float TARGET_HEIGHT = 480f;
	public static final float TARGET_FPS = 70f;
	public static final float PHYSICS_TIMESTEP = 1f/60f; // Use Negative to use Dynamic Time
	public static final boolean PROFILE_SPEED = false;
	public static final boolean PROFILE_STEP_SPEED = false;
	public static final boolean PROFILE_RENDER_SPEED = false;
	public static final int GROUND_ENTITY_THRESHOLD = 3; // How many points should the ground be blocked
	public static final boolean SHOW_FPS = true;
	public static final int FPS_AVG_BUFFER_SIZE = 10;
	public static final boolean DRAW_PHYSICS = true;
	
	public static final boolean ACTIVATE_FIRE = false;
	
	// Should the texture be loaded when they need to be used ?
	public static final boolean LAZY_LOAD_ENABLED = true;
	
	// Bottom-most Y coordinate position that the ground will have, in meters
	public static final float GROUND_BOTTOM = -10;
	public static final float DEFAULT_FONT_SIZE = 60;
	
	// Dynamic
	public static float DisplayWidth = -1;
	public static float DisplayHeight = -1;
	
	public static float WidthRatio = 1f;
	public static float HeightRatio = 1f;
}
