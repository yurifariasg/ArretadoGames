package com.arretadogames.pilot.entities;

import org.jbox2d.collision.shapes.ChainShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.BodyType;

import com.arretadogames.pilot.render.GameCanvas;

public class Ground extends Entity {
	
	private Vec2[] vec;

	public Ground(Vec2[] vec, int count) {
		super(0, 0);
		this.vec = vec;
		ChainShape shape = new ChainShape();
		shape.createChain(vec, count);
		body.createFixture(shape, 0.5f);
		body.setType(BodyType.STATIC);
	}

	@Override
	public void render(GameCanvas canvas, float timeElapsed) {
		for (int i = 1 ; i < vec.length ; i++) {
			Vec2 v1 = vec[i - 1];
			Vec2 v2 = vec[i];
			canvas.drawPhysicsLine(v1.x, v1.y, v2.x, v2.y);
		}
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
