package com.arretadogames.pilot.physics;

import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;
import org.jbox2d.dynamics.contacts.Contact;


public class Entity {
	protected Body body;
	protected World world;
	
	public Entity(float x, float y){
		world = GameWorld.getInstance().getWorld();
		BodyDef bd = new BodyDef();
		bd.position.set(x, y);
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
	
	public void step(){
		
	}
}
