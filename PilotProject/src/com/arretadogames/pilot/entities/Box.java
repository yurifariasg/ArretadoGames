package com.arretadogames.pilot.entities;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.Fixture;

import android.util.Log;

import com.arretadogames.pilot.render.GameCanvas;

public class Box extends Entity {

	public Box(float x, float y, float size) {
		super(x, y);
		PolygonShape shape = new PolygonShape();
		shape.setAsBox(size, size);
		body.createFixture(shape, (float) 0.5);
		body.setType(BodyType.DYNAMIC);
	}

	@Override
	public void render(GameCanvas canvas, float timeElapsed) {
		canvas.drawDebugRect((int)getPosX(), (int)getPosY(), (int)getPosX()+50, (int)getPosY()+60);
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
