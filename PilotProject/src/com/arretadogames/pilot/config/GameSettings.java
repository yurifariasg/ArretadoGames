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
	public static final boolean SHOW_FPS = false;
	public static final int FPS_AVG_BUFFER_SIZE = 10;
	public static final boolean DRAW_PHYSICS = false;
	public static final boolean USE_CRASHLYTICS = false;
    public static final boolean ALWAYS_SHOW_FIRE = false;
	
	public static final boolean ACTIVATE_FIRE = true;
	
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
	
	public static final float INITIAL_RUN_THRESHOLD = 2.5f;
	
	public static final float DASH_MAX_VEL_MULTIPLIER = 1.5f;
	public static final float AFTER_DASH_DAMPING = 1f / 2f;
	
	//LoboGuara
	public static final float LOBO_MAX_JUMP_VELOCITY = 3.5f;
	public static final float LOBO_MAX_RUN_VELOCITY = 5f;
	public static float LOBO_JUMP_ACELERATION = 4.3f;
	public static float LOBO_RUN_ACELERATION = 0.8f;
    public static float LOBO_INITIAL_RUN_ACELERATION = 5;
	public static final float LOBO_TIME_WAITING_FOR_ACT = 6;
	public static final float LOBO_DASH_DURATION = 1f;
	
	//Arara
	public static final float ARARA_MAX_JUMP_VELOCITY = 4;
	public static final float ARARA_MAX_RUN_VELOCITY = 4f;
	public static float ARARA_JUMP_ACELERATION = 3.5f;
	public static float ARARA_RUN_ACELERATION = 0.4f;
	public static float ARARA_INITIAL_RUN_ACELERATION = 4;
	public static final float ARARA_TIME_WAITING_FOR_ACT = 6;
    public static final float ARARA_DASH_DURATION = 0.8f;
	
	//Tatu
	public static final float TATU_MAX_JUMP_VELOCITY = 2f;
	public static final float TATU_MAX_RUN_VELOCITY = 4.5f;
	public static float TATU_JUMP_ACELERATION = 3.5f;
	public static float TATU_RUN_ACELERATION = 0.85f;
	public static float TATU_INITIAL_RUN_ACELERATION = 4;
	public static final float TATU_TIME_WAITING_FOR_ACT = 6;
    public static final float TATU_DASH_DURATION = 0.9f;
	
	//Macaco
	public static final float MACACO_MAX_JUMP_VELOCITY = 8;
	public static final float MACACO_MAX_RUN_VELOCITY = 4f;
	public static float MACACO_JUMP_ACELERATION = 5.5f;
	public static float MACACO_RUN_ACELERATION = 0.7f;
	public static float MACACO_INITIAL_RUN_ACELERATION = 4f;
	public static final float MACACO_TIME_WAITING_FOR_ACT = 6;
    public static final float MACACO_DASH_DURATION = 0.5f;
}
