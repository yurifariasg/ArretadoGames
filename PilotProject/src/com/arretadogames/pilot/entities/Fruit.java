package com.arretadogames.pilot.entities;

import com.arretadogames.pilot.render.GameCanvas;

public class Fruit extends Entity {

	private float size;
	
	public Fruit(float x, float y, float size) {
		super(x, y);
		this.size = size;
	}

	@Override
	public void render(GameCanvas canvas, float timeElapsed) {

	}

	@Override
	public void step() {
		// TODO Auto-generated method stub

	}

	@Override
	public EntityType getType() {
		// TODO Auto-generated method stub
		return null;
	}

}
