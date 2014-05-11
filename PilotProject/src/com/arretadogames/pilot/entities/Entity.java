package com.arretadogames.pilot.entities;

import android.util.Log;

import com.arretadogames.pilot.physics.PhysicalWorld;
import com.arretadogames.pilot.render.AnimationSwitcher;
import com.arretadogames.pilot.render.PhysicsRect;
import com.arretadogames.pilot.render.Renderable;

import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.collision.Manifold;
import org.jbox2d.collision.shapes.Shape;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;
import org.jbox2d.dynamics.contacts.Contact;


public abstract class Entity implements Renderable, LayerEntity {
	
	public enum State {
		ALIVE, DYING, DEAD;
	}
	
	public Body body;
	protected World world;
	protected PhysicsRect physRect;
	protected State state;
	protected boolean isOnWater;

	public Entity(float x, float y) {
		world = PhysicalWorld.getInstance().getWorld();
		BodyDef bd = new BodyDef();
		bd.position.set(x, y);
		bd.userData = this;
		body = world.createBody(bd);
		body.setUserData(this);
		body.setSleepingAllowed(true);
		body.setAwake(false);
		state = State.ALIVE;
	}
	
	@Override
	public int getLayerPosition() {
		return 0;
	}
	
	public void setOnWater(boolean isOnWater) {
	    this.isOnWater = isOnWater;
	}
	
	public boolean isOnWater() {
	    return isOnWater;
	}
	
	public final boolean isAlive() {
		return state != State.DEAD;
	}
	
	public final boolean isDead() {
		return state == State.DEAD;
	}
	
	/**
	 * Subclasses may override this to capture the kill event
	 */
	public void kill() {
		if (!isAlive()) {
			Log.i("Entity", "Trying to kill a entity that is not alive");
		} else {
			state = State.DEAD;
		}
	}

	public void addFixture(FixtureDef fd){
		body.createFixture(fd);
	}
	
	public float getPosX(){
		return body.getPosition().x;
	}
	
	public float getPosY(){
		return body.getPosition().y;
	}
	
	public void beginContact(Entity e, Contact contact) {
		
	}

	public void endContact(Entity e , Contact contact) {
		
	}
	
	public abstract EntityType getType();
	
	public abstract void setSprite(AnimationSwitcher sprite);

	public void destroyBody() {
		world.destroyBody(body);
	}

	public void preSolve(Entity e, Contact contact, Manifold oldManifold) {
	}

	public void postSolve(Entity e, Contact contact, ContactImpulse impulse) {
	}

	public Shape getWaterContactShape() {
		return body.m_fixtureList.m_shape;
	}
	
}
