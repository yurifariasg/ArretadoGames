package com.arretadogames.pilot.entities.scenario;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.dynamics.BodyType;

import com.arretadogames.pilot.entities.Entity;
import com.arretadogames.pilot.render.PhysicsRect;
import com.arretadogames.pilot.render.opengl.GLCanvas;

public abstract class ScenarioEntity extends Entity {
	
	private float width;
	private float height;

	public ScenarioEntity(float x, float y, float width, float height) {
		super(x, y);
		
		PolygonShape shape = new PolygonShape();
		shape.setAsBox(width / 2, height / 2);
		body.createFixture(shape, 1.0f).setSensor(true);
		body.setType(BodyType.STATIC); // All Scenario Entities don't move
		
		this.width = width;
		this.height = height;
		physRect = new PhysicsRect(width, height);
	}
	
	public float getWidth() {
		return width;
	}
	
	public float getHeight() {
		return height;
	}
	
	protected void drawBasic(GLCanvas canvas, int resourceId) {
		canvas.saveState();
		
		canvas.translatePhysics(getPosX(), getPosY());

		canvas.drawBitmap(resourceId, physRect);
		
		canvas.restoreState();
	}

}
