package com.arretadogames.pilot.entities;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.contacts.Contact;

import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.RectF;

import com.arretadogames.pilot.physics.PhysicalWorld;
import com.arretadogames.pilot.render.Sprite;
import com.arretadogames.pilot.render.opengl.GLCanvas;

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
			PhysicalWorld.getInstance().addDeadEntity(this);
		}
	}

	@Override
	public void render(GLCanvas canvas, float timeElapsed) {
		canvas.saveState();
		canvas.translatePhysics(getPosX(), getPosY());
		canvas.rotate((float) (180 * - body.getAngle() / Math.PI));
		RectF rect = new RectF(
				(- size/2 * GLCanvas.physicsRatio), // Top Left
				(- size/2 * GLCanvas.physicsRatio), // Top Left
				(size/2 * GLCanvas.physicsRatio), // Bottom Right
				(size/2 * GLCanvas.physicsRatio)); // Bottom Right
		
		canvas.drawRect(rect.left, rect.top,
				rect.right, rect.bottom, Color.RED);
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
