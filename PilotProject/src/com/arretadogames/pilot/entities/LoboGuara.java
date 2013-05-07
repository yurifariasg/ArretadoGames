package com.arretadogames.pilot.entities;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.dynamics.BodyType;

import android.graphics.Color;

import com.arretadogames.pilot.render.GameCanvas;

public class LoboGuara extends Player {
	
	public LoboGuara(float x, float y, PlayerNumber number) {
		super(1f, 10f, number);
		PolygonShape shape = new PolygonShape();
		shape.setAsBox(0.5f, 0.5f); // FIXME Check this size
		body.createFixture(shape, (float) 0.5);
		body.setType(BodyType.DYNAMIC);
	}

	@Override
	public void render(GameCanvas canvas, float timeElapsed) {
		// TODO Auto-generated method stub
//		canvas.drawDebugRect((int)getPosX(), (int)getPosY(),
//				(int)(getPosX() ), (int)(getPosY()+size));
		canvas.drawPhysicsDebugRect(getPosX(), getPosY(), 0.5f, Color.BLUE);
		
	}

	@Override
	public void jump() {
		// TODO create impulse
		
	}

	@Override
	public void act() {
		// TODO stop moving for awhile or do something else...
	}

	@Override
	public void step() {
		// TODO Logic using body
	}

}
