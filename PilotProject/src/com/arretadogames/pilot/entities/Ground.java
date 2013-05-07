package com.arretadogames.pilot.entities;

import org.jbox2d.collision.shapes.ChainShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.BodyType;

import com.arretadogames.pilot.render.GameCanvas;

public class Ground extends Entity {

	public Ground(Vec2[] vec, int count) {
		super(0, 0);
		ChainShape shape = new ChainShape();
		shape.createChain(vec, count);
		body.createFixture(shape, 0.5f);
		body.setType(BodyType.STATIC);
	}

	@Override
	public void render(GameCanvas canvas, float timeElapsed) {
		// TODO Auto-generated method stub

	}

	@Override
	public void step() {
		// TODO Auto-generated method stub

	}

	@Override
	public EntityType getType() {
		return EntityType.GROUND;
	}

}
