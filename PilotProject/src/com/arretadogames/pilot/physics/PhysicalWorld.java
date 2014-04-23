package com.arretadogames.pilot.physics;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.collision.Manifold;
import org.jbox2d.collision.shapes.ChainShape;
import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.EdgeShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.World;
import org.jbox2d.dynamics.contacts.Contact;
import org.jbox2d.dynamics.joints.Joint;

import android.graphics.Color;

import com.arretadogames.pilot.config.GameSettings;
import com.arretadogames.pilot.entities.Entity;
import com.arretadogames.pilot.render.Renderable;
import com.arretadogames.pilot.render.opengl.GLCanvas;
import com.arretadogames.pilot.render.opengl.GLCircle;
import com.arretadogames.pilot.util.Profiler;
import com.arretadogames.pilot.util.Profiler.ProfileType;


public class PhysicalWorld implements ContactListener, Renderable {
	private static PhysicalWorld gworld;
	World world;
	private Collection<Entity> deadEntities;
	
	private PhysicalWorld() {
		world = new World(new Vec2(0.0f,-6.0f));
		world.setAutoClearForces(true);
		world.setContactListener(this);
		deadEntities = Collections.synchronizedCollection(new ArrayList<Entity>());
	}
	
	public static PhysicalWorld getInstance() {
		if(gworld == null) {
			gworld = new PhysicalWorld();
		}
		return gworld;
	}
	
	/**
	 * Puts all bodies to sleep
	 */
	public void sleepAllEntities() {
		Body body = world.getBodyList();
		while (body != null) {
			body.setAwake(false);
			body = body.getNext();
		}
	}
	
	public void removeAll() {
		Body b = getWorld().getBodyList();
		while (b != null) { // Remove Bodies
			getWorld().destroyBody(b);
			b = b.getNext();
		}
		
		Joint j = getWorld().getJointList();
		while (j != null) { // Remove Bodies
			getWorld().destroyJoint(j);
			j = j.getNext();
		}
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
		Entity a = (Entity)contact.m_fixtureA.getBody().getUserData();
		Entity b = (Entity)contact.m_fixtureB.getBody().getUserData();
		a.postSolve(b, contact, impulse);
		b.postSolve(a, contact, impulse);
	}

	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {
		Entity a = (Entity)contact.m_fixtureA.getBody().getUserData();
		Entity b = (Entity)contact.m_fixtureB.getBody().getUserData();
		a.preSolve(b, contact, oldManifold);
		b.preSolve(a, contact, oldManifold);
	}

	public void step(float timeElapsed) {
		Profiler.initTick(ProfileType.STEP);
		
		world.step(GameSettings.PHYSICS_TIMESTEP < 0 ?
				timeElapsed : GameSettings.PHYSICS_TIMESTEP, 16, 6);
		
		Profiler.profileFromLastTick(ProfileType.STEP, "Box2D World Step Time");
		
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
		e.destroyBody();
	}

	@Override
	public void render(GLCanvas canvas, float timeElapsed) {
		//Render All bodies
		Body body = world.getBodyList();
		
		while (body != null) {
			
			Fixture fixture = body.getFixtureList();
			
			canvas.saveState();
			
			while (fixture != null) {
			    
				canvas.saveState();
				
				switch (fixture.getShape().getType()) {
				
				case POLYGON:
					canvas.translatePhysics(body.getPosition().x, body.getPosition().y);
					canvas.rotate((float) (180 *  (-body.getAngle()) / Math.PI));
					drawPolygon(canvas, (PolygonShape) fixture.getShape());
					break;
				case CIRCLE:
					CircleShape circleShape =  (CircleShape) fixture.getShape();
					canvas.translatePhysics(body.getPosition().x + circleShape.m_p.x, body.getPosition().y + circleShape.m_p.y);
					canvas.rotate((float) (180 *  (-body.getAngle()) / Math.PI));
					drawCircle(canvas, (CircleShape) fixture.getShape());
					break;
					
				case CHAIN:
					drawChain(canvas, (ChainShape) fixture.getShape());
					break;
				case EDGE:
					canvas.translatePhysics(body.getPosition().x, body.getPosition().y);
					canvas.rotate((float) (180 *  (-body.getAngle()) / Math.PI));
					drawEdge(canvas, (EdgeShape) fixture.getShape());
					break;
				}
			    
				canvas.restoreState();
				
				fixture = fixture.getNext();
			}
			
			canvas.restoreState();
			
			body = body.getNext();
		}
	}
	
	private Vec2[] auxVec = new Vec2[2];

	private void drawEdge(GLCanvas canvas, EdgeShape shape) {
		
		auxVec[0] = shape.m_vertex1;
		auxVec[1] = shape.m_vertex2;
		canvas.drawLines(auxVec, 3, Color.YELLOW, false);
		
	}

	private void drawChain(GLCanvas canvas, ChainShape shape) {
		canvas.drawGroundLines(shape.m_vertices, shape.m_vertices.length, 3, Color.WHITE);
	}

	private void drawCircle(GLCanvas canvas, CircleShape shape) {
		new GLCircle(shape.m_radius * GLCanvas.physicsRatio).drawCircle(canvas, 0, 0, Color.YELLOW, 3, false);
	}

	private void drawPolygon(GLCanvas canvas, PolygonShape shape) {
		canvas.drawPhysicsLines(shape.getVertices(), shape.getVertexCount(), 3, Color.YELLOW, true, true);
		
	}
}
