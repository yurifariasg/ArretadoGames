package com.arretadogames.pilot.physics;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.collision.Manifold;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.World;
import org.jbox2d.dynamics.contacts.Contact;
import org.jbox2d.dynamics.joints.Joint;

import com.arretadogames.pilot.config.DisplaySettings;
import com.arretadogames.pilot.entities.Entity;


public class PhysicalWorld implements ContactListener {
	private static PhysicalWorld gworld;
	World world;
	private Collection<Entity> deadEntities;
	
	private PhysicalWorld() {
		world = new World(new Vec2(0.0f,-10.0f));
		world.setContactListener(this);
		world.setAllowSleep(true);
		deadEntities = new ArrayList<Entity>();
	}
	
	public static PhysicalWorld getInstance() {
		if(gworld == null) {
			gworld = new PhysicalWorld();
		}
		return gworld;
	}
	
	public static void restart() {
		Body b = gworld.world.getBodyList();
		while (b != null) { // Remove Bodies
			gworld.getWorld().destroyBody(b);
			b = b.getNext();
		}
		
		Joint j = gworld.world.getJointList();
		while (j != null) { // Remove Bodies
			gworld.getWorld().destroyJoint(j);
			j = j.getNext();
		}
		
		gworld.world = null;
		gworld = null;
	}
	
	public World getWorld() {
		return world;
	}

	@Override
	public void beginContact(Contact contact) {
		Entity a = (Entity)contact.m_fixtureA.getBody().getUserData();
		Entity b = (Entity)contact.m_fixtureB.getBody().getUserData();
		a.beginContact(b, contact);
		b.beginContact(a, contact);
	}

	@Override
	public void endContact(Contact contact) {
		Entity a = (Entity)contact.m_fixtureA.getBody().getUserData();
		Entity b = (Entity)contact.m_fixtureB.getBody().getUserData();
		a.endContact(b, contact);
		b.endContact(a, contact);
	}

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {
		// TODO Auto-generated method stub
		
	}

	public void step(float timeElapsed) {
		world.step(DisplaySettings.PHYSICS_STEP < 0 ?
				timeElapsed : DisplaySettings.PHYSICS_STEP, 8, 10);
	}

	public void addDeadEntity(Entity e) {
		deadEntities.add(e);
	}
	
	public void clearDeadEntities(){
		deadEntities.clear();
	}
	
	public Iterator<Entity> getDeadEntities(){
		return deadEntities.iterator();
	}
	
	public void destroyEntity(Entity e){
		world.destroyBody(e.body);
	}
}
