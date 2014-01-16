package com.arretadogames.pilot.entities;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.contacts.Contact;

import android.graphics.Color;
import android.graphics.RectF;

import com.arretadogames.pilot.R;
import com.arretadogames.pilot.render.PhysicsRect;
import com.arretadogames.pilot.render.Sprite;
import com.arretadogames.pilot.render.opengl.GLCanvas;

public class FinalFlag extends Entity {

	private static final int[] STOPPED = {};
	
	private float width;
	private float height;
	
	public FinalFlag(float x, float y) {
		super(x, y);
		
		PolygonShape shape = new PolygonShape();
		width = 0.2f;
		height = 20;
		shape.setAsBox(width/2, height/2);
		Fixture a = body.createFixture(shape,  1f);
		a.setSensor(true);
		body.setType(BodyType.KINEMATIC);
		body.setFixedRotation(false);
		
		physRect = new PhysicsRect(width, height);
	}
	
	@Override
	public void beginContact(Entity e, Contact contact) {
		super.beginContact(e, contact);
		if(e.getType() == EntityType.PLAYER ){
			((Player)e).setFinished(true); // Finished Stage
		}
	}

	@Override
	public void render(GLCanvas canvas, float timeElapsed) {
		canvas.saveState();
		canvas.translatePhysics(getPosX(), getPosY());
		canvas.rotate((float) (180 * - body.getAngle() / Math.PI));
		RectF rect = new RectF(
				(- width * GLCanvas.physicsRatio), // Top Left
				(- height * GLCanvas.physicsRatio), // Top Left
				(width * GLCanvas.physicsRatio), // Bottom Right
				(height * GLCanvas.physicsRatio)); // Bottom Right
		
		canvas.drawRect((int) rect.left, (int) rect.top, (int) rect.right, (int) rect.bottom, Color.RED);
//		canvas.drawBitmap(sprite.getCurrentFrame(timeElapsed), physRect);
//		canvas.drawBitmap(sprite.getCurrentFrame(timeElapsed), rect, false);
		canvas.restoreState();
	}

	@Override
	public EntityType getType() {
		return EntityType.FINALFLAG;
	}
	
	public int[] getStoppedFrames() {
		return STOPPED;
	}
	
	public float[] getStoppedFramesDuration(){
		return new float[] {0.3f, 0.3f};
	}

	public void setSprite(Sprite sprite){
	}

}
