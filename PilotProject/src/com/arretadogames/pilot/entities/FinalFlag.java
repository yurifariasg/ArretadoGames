package com.arretadogames.pilot.entities;

import com.arretadogames.pilot.R;
import com.arretadogames.pilot.render.PhysicsRect;
import com.arretadogames.pilot.render.AnimationSwitcher;
import com.arretadogames.pilot.render.opengl.GLCanvas;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.contacts.Contact;

public class FinalFlag extends Entity {

	private static final int[] STOPPED = {};
	
	private float width;
	private float height;
	
	public FinalFlag(float x, float y) { // y is always ignored
		super(x, 0);
		
		PolygonShape shape = new PolygonShape();
		width = 0.2f;
		height = 20;
		shape.setAsBox(width/2, height/2);
		Fixture a = body.createFixture(shape,  1f);
		a.setSensor(true);
		body.setType(BodyType.KINEMATIC);
		body.setFixedRotation(false);
		
		physRect = new PhysicsRect(1, 2);
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
		canvas.translatePhysics(getPosX(), getPosY() + 0.2f); // Offset distance to make flag hit the ground (adjust according to sprite)
		canvas.drawBitmap(R.drawable.flag, physRect);
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

	public void setSprite(AnimationSwitcher sprite){
	}

}
