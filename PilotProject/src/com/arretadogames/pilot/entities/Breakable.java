package com.arretadogames.pilot.entities;

import com.arretadogames.pilot.R;
import com.arretadogames.pilot.physics.PhysicalWorld;
import com.arretadogames.pilot.render.PhysicsRect;
import com.arretadogames.pilot.render.AnimationSwitcher;
import com.arretadogames.pilot.render.opengl.GLCanvas;

import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.MathUtils;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.contacts.Contact;

public class Breakable extends Entity implements Steppable{
	
	private boolean m_broke;
	private boolean m_break;

	private int life = 5;

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
		
		physRect = new PhysicsRect(width, height);
	}

	@Override
	public void render(GLCanvas canvas, float timeElapsed) {
		
		canvas.saveState();
		canvas.translatePhysics(body.getPosition().x, body.getPosition().y);
		canvas.rotate((float) (180 * - body.getAngle() / Math.PI));
		if( life > 3 )canvas.drawBitmap(R.drawable.muro_inteiro, physRect);
		else canvas.drawBitmap(R.drawable.muro_quebrado, physRect);
		canvas.restoreState();

	}
	
	@Override
	public void postSolve(Entity e, Contact contact, ContactImpulse impulse) {
		if (m_broke) return;
		int count = contact.getManifold().pointCount;
		float maxImpulse = 0.0f;
		for (int i = 0; i < count; ++i) {
			maxImpulse = MathUtils.max(maxImpulse, impulse.normalImpulses[i]);
		}
//		if (maxImpulse > 6.0f) {
//			m_break = true;
//		}
		life -= (int)((maxImpulse)/2);
	}

	@Override
	public void step(float timeElapsed) {
		if(life  <= 0) Break();
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
	public void setSprite(AnimationSwitcher sprite) {
	}
}
