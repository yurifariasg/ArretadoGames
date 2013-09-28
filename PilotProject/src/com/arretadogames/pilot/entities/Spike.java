package com.arretadogames.pilot.entities;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.contacts.Contact;

import android.graphics.RectF;

import com.arretadogames.pilot.R;
import com.arretadogames.pilot.physics.PhysicalWorld;
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
		
	}

	@Override
	public void render(GLCanvas canvas, float timeElapsed) {
		canvas.saveState();
		canvas.translatePhysics(this.getPosX(), this.getPosY());
		canvas.rotate((float) (180 * - body.getAngle() / Math.PI));
		RectF rect = new RectF(
				(- width/2 * GLCanvas.physicsRatio), // Top Left X
				((- height/2) * GLCanvas.physicsRatio), // Top Left Y
				(width/2 * GLCanvas.physicsRatio), // Bottom Right X
				((height/2) * GLCanvas.physicsRatio)); // Bottom Right Y

//		canvas.drawRect((int) rect.left, (int) rect.top, (int) rect.right, (int) rect.bottom, Color.DKGRAY);
		canvas.drawBitmap(sprite.getCurrentFrame(timeElapsed), rect, false);
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
