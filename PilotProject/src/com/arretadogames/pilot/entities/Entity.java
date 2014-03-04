package com.arretadogames.pilot.entities;

import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.collision.Manifold;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;
import org.jbox2d.dynamics.contacts.Contact;

import com.arretadogames.pilot.physics.PhysicalWorld;
import com.arretadogames.pilot.render.PhysicsRect;
import com.arretadogames.pilot.render.Renderable;
import com.arretadogames.pilot.render.AnimationSwitcher;


public abstract class Entity implements Renderable, LayerEntity {
	
	public Body body;
	protected World world;
	private boolean isDead;
	protected PhysicsRect physRect;

	public Entity(float x, float y) {
		world = PhysicalWorld.getInstance().getWorld();
		BodyDef bd = new BodyDef();
		bd.position.set(x, y);
		bd.userData = this;
		body = world.createBody(bd);
		body.setUserData(this);
		body.setSleepingAllowed(true);
		body.setAwake(false);
		isDead = false;
	}
	
	@Override
	public int getLayerPosition() {
		return 0;
	}
	
	public boolean isAlive() {
		return !isDead;
	}
	
	public void setDead(boolean isDead) {
		this.isDead = isDead;
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

	public void preSolve(Contact contact, Manifold oldManifold) {
	}

	public void postSolve(Contact contact, ContactImpulse impulse) {
	}

	public PolygonShape getWaterContactShape() {
		return (PolygonShape) body.m_fixtureList.m_shape;
	}
	
}
