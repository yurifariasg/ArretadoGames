package com.arretadogames.pilot.entities;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.Fixture;

import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.RectF;

import com.arretadogames.pilot.render.Sprite;
import com.arretadogames.pilot.render.opengl.GLCanvas;

public class Fire extends Entity{

	private float height;
	private float width;
	
	public Fire(float x, float y) {
		super(x, y);
		PolygonShape shape = new PolygonShape();
		width = 1;
		height = 1;
		shape.setAsBox(width/2, height/2);
		Fixture f = body.createFixture(shape,  0f);
		f.setSensor(true);
		body.setType(BodyType.DYNAMIC);
		body.setLinearVelocity(new Vec2(1,0));
	}

	@Override
	public void render(GLCanvas canvas, float timeElapsed) {
		canvas.saveState();
		canvas.translatePhysics(getPosX(), getPosY());
		//canvas.rotate((float) (180 * - body.getAngle() / Math.PI));
		RectF rect = new RectF(
				(- width/2 * GLCanvas.physicsRatio), // Top Left
				(- height/2 * GLCanvas.physicsRatio), // Top Left
				(width/2 * GLCanvas.physicsRatio), // Bottom Right
				(height/2 * GLCanvas.physicsRatio)); // Bottom Right
		
		canvas.drawRect(new Rect((int) rect.left, (int) rect.top, (int) rect.right, (int) rect.bottom), Color.YELLOW);
		canvas.restoreState();
	}

	@Override
	public void step(float timeElapsed) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public EntityType getType() {
		return EntityType.FIRE;
	}

	@Override
	public void setSprite(Sprite sprite) {
		// TODO Auto-generated method stub
		
	}

}
