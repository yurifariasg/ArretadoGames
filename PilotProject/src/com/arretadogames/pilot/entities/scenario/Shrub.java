package com.arretadogames.pilot.entities.scenario;

import com.arretadogames.pilot.R;
import com.arretadogames.pilot.entities.EntityType;
import com.arretadogames.pilot.render.Sprite;
import com.arretadogames.pilot.render.opengl.GLCanvas;

public class Shrub extends ScenarioEntity {
	
	private static int[] SHRUB_RESOURCES = {
		R.drawable.shrub // We may change where this is currently located when we refactor sprites 
	};
	
	// Tree Sizes
	private static float[][] SHRUB_SIZES = { // {width, height}
		{0.5f, 0.5f}, // Tree Type 0
	};
	
	// Object Properties
	private int shrubType;
	
	public Shrub(float x, float y) {
		this(x, y, 0);
	}
	
	public Shrub(float x, float y, int type) {
		super(x, y, SHRUB_SIZES[type][0], SHRUB_SIZES[type][1]);
		this.shrubType = type;
	}

	@Override
	public void render(GLCanvas canvas, float timeElapsed) {
		drawBasic(canvas, SHRUB_RESOURCES[shrubType]);
	}

	@Override
	public EntityType getType() {
		return EntityType.SHRUB;
	}

	@Override
	public void setSprite(Sprite sprite) {
		// TODO Remove this when sprites are refactored
	}
	
	@Override
	public int getLayerPosition() {
		return 9;
	}
	
}
