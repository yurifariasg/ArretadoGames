package com.arretadogames.pilot.entities;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.dynamics.BodyType;

import com.arretadogames.pilot.render.GameCanvas;

public class Box extends Entity {

	private float size;
	public Box(float x, float y, float size) {
		super(x, y);
		this.size = size;
		PolygonShape shape = new PolygonShape();
		shape.setAsBox(size/2, size/2);
		body.createFixture(shape, (float) 0.5);
		body.setType(BodyType.DYNAMIC);
		body.setFixedRotation(false);
		
	}

	@Override
	public void render(GameCanvas canvas, float timeElapsed) {
		canvas.saveState();
		canvas.rotatePhysics((float) (180 * - body.getAngle() / Math.PI), getPosX(), getPosY());
		canvas.drawPhysicsDebugRect(getPosX(), getPosY(), size );
		canvas.restoreState();
	}

	@Override
	public void step(float timeElapsed) {
		// TODO Box Logic
	}

	@Override
	public EntityType getType() {
		return EntityType.BOX;
	}

}
