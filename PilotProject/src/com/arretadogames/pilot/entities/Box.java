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
		shape.setAsBox(size, size);
		body.createFixture(shape, (float) 0.5);
		body.setType(BodyType.DYNAMIC);
	}

	@Override
	public void render(GameCanvas canvas, float timeElapsed) {
		//Log.d("posss", "x " +getPosX() + " y: " + getPosY());
//		canvas.drawDebugRect((int)getPosX(), (int)getPosY(), (int)(getPosX()+size), (int)(getPosY()+size));
		canvas.drawPhysicsDebugRect(getPosX(), getPosY(), size * 2);
//		canvas.drawDebugRect((int)getPosX(),- (int)getPosY(), (int)(getPosX()+size*50), -(int)(getPosY()+size*50));
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
