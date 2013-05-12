package com.arretadogames.pilot.entities;

import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;
import org.jbox2d.dynamics.contacts.Contact;

import com.arretadogames.pilot.physics.PhysicalWorld;
import com.arretadogames.pilot.render.Renderable;


public abstract class Entity implements Renderable {
	public Body body;
	protected World world;
	
	public Entity(float x, float y) {
		world = PhysicalWorld.getInstance().getWorld();
		BodyDef bd = new BodyDef();
		bd.position.set(x, y);
		bd.userData = this;
		body = world.createBody(bd);
		body.setUserData(this);
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
	
	public abstract void step(float timeElapsed);
	
	public abstract EntityType getType();
	
}
