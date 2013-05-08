package com.arretadogames.pilot.physics;
import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.collision.Manifold;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.World;
import org.jbox2d.dynamics.contacts.Contact;
import com.arretadogames.pilot.entities.Entity;


public class PhysicalWorld implements ContactListener {
	private static PhysicalWorld gworld;
	World world;
	
	private PhysicalWorld() {
		world = new World(new Vec2(0.0f,-10.0f));
		world.setContactListener(this);
	}
	
	public static PhysicalWorld getInstance() {
		if(gworld == null) {
			gworld = new PhysicalWorld();
		}
		return gworld;
	}
	
	public World getWorld() {
		return world;
	}

	@Override
	public void beginContact(Contact contact) {
		System.out.println("guilherme eh gay");
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
		world.step(timeElapsed, 8, 10);
	}
}
