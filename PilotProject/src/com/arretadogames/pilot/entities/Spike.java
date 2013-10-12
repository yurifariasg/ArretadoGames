package com.arretadogames.pilot.entities;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.contacts.Contact;

import com.arretadogames.pilot.R;
import com.arretadogames.pilot.physics.PhysicalWorld;
import com.arretadogames.pilot.render.PhysicsRect;
import com.arretadogames.pilot.render.Sprite;
import com.arretadogames.pilot.render.opengl.GLCanvas;

public class Spike extends Entity{
	
	private float width;
	private float height;
	private Sprite sprite;
	private static final int[] STOPPED = {R.drawable.spike};

	public Spike(float x, float y) {
		super(x, y);
		width = 3;
		height = 0.1f;
		PolygonShape shape = new PolygonShape();
		shape.setAsBox(width/2, height/2);
		body.createFixture(shape, 1f).setSensor(true);
		body.setType(BodyType.KINEMATIC);
		
		physRect = new PhysicsRect(width, height);
	}

	@Override
	public void render(GLCanvas canvas, float timeElapsed) {
		canvas.saveState();
		canvas.translatePhysics(this.getPosX(), this.getPosY());
		canvas.rotate((float) (180 * - body.getAngle() / Math.PI));
		canvas.drawBitmap(R.drawable.sheave, physRect);
		canvas.restoreState();
	}
	
	@Override
	public void beginContact(Entity e, Contact contact) {
		super.beginContact(e, contact);
		if (e.getType() == EntityType.PLAYER && isAlive()) {
			Player p = (Player) e;
			p.setDead(true);
			PhysicalWorld.getInstance().addDeadEntity(p);
		}
	}
	
	@Override
	public EntityType getType() {
		return EntityType.SPIKE; 
	}

	@Override
	public void setSprite(Sprite sprite) {
		this.sprite = sprite;
	}
	
	public int[] getStoppedFrames() {
		return STOPPED;
	}
	
	public float[] getStoppedFramesDuration(){
		return new float[] {-1f};
	}

}
