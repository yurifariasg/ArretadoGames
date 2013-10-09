package com.arretadogames.pilot.entities;

import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.MathUtils;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.contacts.Contact;

import android.graphics.RectF;

import com.arretadogames.pilot.R;
import com.arretadogames.pilot.physics.PhysicalWorld;
import com.arretadogames.pilot.render.Sprite;
import com.arretadogames.pilot.render.opengl.GLCanvas;

public class Breakable extends Entity implements Steppable{
	
	private static final int[] STOPPED = {R.drawable.breakable};

	private float width;
	private float height;
	private boolean m_broke;
	private boolean m_break;

	private Sprite sprite;

	/**
	 * 
	 * @param x center x position
	 * @param y center y position
	 * @param width
	 * @param height
	 * @param angle radians. 0 -> standing 
	 * @param dynamic if it is dynamic body or static
	 */
	public Breakable(float x, float y, float width, float height, float angle, boolean dynamic) {
		super(x, y);
		this.width = width;
		this.height = height;
		if( dynamic ){ 
			body.setType(BodyType.DYNAMIC);
		} else {
			body.setType(BodyType.STATIC);
		}
		body.setTransform(body.getPosition(), angle);
		PolygonShape shape = new PolygonShape();
		shape.setAsBox(width/2, height/2);
		Fixture fix = body.createFixture(shape, 3f);
		fix.setFriction(0.8f);
		body.setUserData(this);
		m_break = false;
		m_broke = false;
	
	}

	@Override
	public void render(GLCanvas canvas, float timeElapsed) {
		
		canvas.saveState();
		canvas.translatePhysics(body.getPosition().x, body.getPosition().y);
		canvas.rotate((float) (180 * - body.getAngle() / Math.PI));
		RectF rect = new RectF(
				(- width/2 * GLCanvas.physicsRatio), // Top Left
				(- height/2 * GLCanvas.physicsRatio), // Top Left
				(width/2 * GLCanvas.physicsRatio), // Bottom Right
				(height/2 * GLCanvas.physicsRatio)); // Bottom Right

//		canvas.drawRect(new Rect((int) rect.left, (int) rect.top, (int) rect.right, (int) rect.bottom), Color.RED);
		canvas.drawBitmap(sprite.getCurrentFrame(timeElapsed), rect, false);
		canvas.restoreState();

	}
	
	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {
		if (m_broke) return;
		int count = contact.getManifold().pointCount;
		float maxImpulse = 0.0f;
		for (int i = 0; i < count; ++i) {
			maxImpulse = MathUtils.max(maxImpulse, impulse.normalImpulses[i]);
		}
		if (maxImpulse > 6.0f) {
			m_break = true;
		}
	}

	@Override
	public void step(float timeElapsed) {
		if(m_break) Break();
	}

	private void Break() {
		m_broke = true;
		PhysicalWorld.getInstance().addDeadEntity(this);
		//TODO breaking animation
	}

	@Override
	public EntityType getType() {
		return EntityType.BREAKABLE;
	}

	@Override
	public void setSprite(Sprite sprite) {
		this.sprite = sprite;
	}
	
	public int[] getStoppedFrames() {
		return STOPPED;
	}
	
	public float[] getStoppedFramesDuration(){
		return new float[] {-1};
	}
}
