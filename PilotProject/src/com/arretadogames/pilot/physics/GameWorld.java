package com.arretadogames.pilot.physics;
import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.collision.Manifold;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.World;
import org.jbox2d.dynamics.contacts.Contact;


public class GameWorld implements ContactListener{
	private static GameWorld gworld;
	World world;
	
	private GameWorld(){
		world = new World(new Vec2(0.0f,10.0f), true);
	}
	
	public static GameWorld getInstance(){
		if(gworld == null) {
			gworld = new GameWorld();
		}
		return gworld;
	}
	
	World getWorld(){
		return world;
	}

	@Override
	public void beginContact(Contact contact) {
		// TODO Auto-generated method stub
	}

	@Override
	public void endContact(Contact contact) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {
		// TODO Auto-generated method stub
		
	}
}
