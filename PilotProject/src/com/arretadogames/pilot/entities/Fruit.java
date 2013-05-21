package com.arretadogames.pilot.entities;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.contacts.Contact;

import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;

import com.arretadogames.pilot.R;
import com.arretadogames.pilot.render.GameCanvas;
import com.arretadogames.pilot.render.Sprite;
import com.arretadogames.pilot.render.opengl.OpenGLCanvas;

public class Fruit extends Entity {

	private float size;
	
	public Fruit(float x, float y, float size) {
		super(x, y);
		this.size = size;
		
		PolygonShape shape = new PolygonShape();
		shape.setAsBox(size/2, size/2);
		Fixture a = body.createFixture(shape,  1f);
		body.setType(BodyType.DYNAMIC);
		body.setFixedRotation(false);
		this.size = size;
	}
	
	@Override
	public void beginContact(Entity e, Contact contact) {
		super.beginContact(e, contact);
		if(e.getType() == EntityType.PLAYER ){
			System.out.println("Encostou fruta");
		}
	}

	@Override
	public void render(GameCanvas canvas, float timeElapsed) {
		canvas.saveState();
		canvas.translatePhysics(getPosX(), getPosY());
		canvas.rotate((float) (180 * - body.getAngle() / Math.PI));
		RectF rect = new RectF(
				(- size/2 * OpenGLCanvas.physicsRatio), // Top Left
				(- size/2 * OpenGLCanvas.physicsRatio), // Top Left
				(size/2 * OpenGLCanvas.physicsRatio), // Bottom Right
				(size/2 * OpenGLCanvas.physicsRatio)); // Bottom Right
		
		canvas.drawRect(new Rect((int) rect.left, (int) rect.top, (int) rect.right, (int) rect.bottom), Color.RED);
//		canvas.drawBitmap(sprite.getCurrentFrame(timeElapsed), rect, false);
		canvas.restoreState();
	}

	@Override
	public void step(float timeElapsed) {
		// TODO Auto-generated method stub

	}

	@Override
	public EntityType getType() {
		return EntityType.FRUIT;
	}

	@Override
	public void setSprite(Sprite sprite) {
	}

}
