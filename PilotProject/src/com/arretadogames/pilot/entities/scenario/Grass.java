package com.arretadogames.pilot.entities.scenario;

import com.arretadogames.pilot.R;
import com.arretadogames.pilot.entities.EntityType;
import com.arretadogames.pilot.render.Sprite;
import com.arretadogames.pilot.render.opengl.GLCanvas;

public class Grass extends ScenarioEntity {
	
	private static int[] GRASS_RESOURCES = {
		R.drawable.grass // We may change where this is currently located when we refactor sprites 
	};
	
	// Tree Sizes
	private static float[][] GRASS_SIZES = { // {width, height}
		{0.4f, 0.4f}, // Tree Type 0
	};
	
	// Object Properties
	private int grassType;
	
	public Grass(float x, float y) {
		this(x, y, 0);
	}
	
	public Grass(float x, float y, int type) {
		super(x, y, GRASS_SIZES[type][0], GRASS_SIZES[type][1]);
		this.grassType = type;
	}

	@Override
	public void render(GLCanvas canvas, float timeElapsed) {
		drawBasic(canvas, GRASS_RESOURCES[grassType]);
	}

	@Override
	public EntityType getType() {
		return EntityType.GRASS;
	}

	@Override
	public void setSprite(Sprite sprite) {
		// TODO Remove this when sprites are refactored
	}
	
	@Override
	public int getLayerPosition() {
		return 10;
	}
	
}
