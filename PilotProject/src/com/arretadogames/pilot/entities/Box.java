package com.arretadogames.pilot.entities;

import com.arretadogames.pilot.render.GameCanvas;

public class Box extends Entity {

	public Box(float x, float y) {
		super(x, y);
	}

	@Override
	public void render(GameCanvas canvas, float timeElapsed) {
		// TODO Box Rendering
		canvas.drawDebugRect(100, 10, 160, 60);
	}

	@Override
	public void step() {
		// TODO Box Logic
	}

	@Override
	public EntityType getType() {
		return EntityType.BOX;
	}

}
