package com.arretadogames.pilot.physics;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

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
import com.arretadogames.pilot.entities.Water;
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
		world.setContactListener(this);
		world.setAllowSleep(true);
		deadEntities = Collections.synchronizedCollection(new ArrayList<Entity>());
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
		Entity a = (Entity)contact.m_fixtureA.getBody().getUserData();
		Entity b = (Entity)contact.m_fixtureB.getBody().getUserData();
		a.postSolve(contact, impulse);
		b.postSolve(contact, impulse);
	}

	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {
		Entity a = (Entity)contact.m_fixtureA.getBody().getUserData();
		Entity b = (Entity)contact.m_fixtureB.getBody().getUserData();
		a.preSolve(contact, oldManifold);
		b.preSolve(contact, oldManifold);
	}

	public void step(float timeElapsed) {
		Profiler.initTick(ProfileType.STEP);
		
		world.step(GameSettings.PHYSICS_TIMESTEP < 0 ?
				timeElapsed : GameSettings.PHYSICS_TIMESTEP, 8, 10);
		
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

	/**
	 * Create ground based on given entities
	 */
	public Vec2[] createGroundLines(List<Water> waterEntities, float lastX) {
		
		// Sort Based on X
		Collections.sort(waterEntities, new Comparator<Water>() {
			@Override
			public int compare(Water lhs, Water rhs) {
				return (int) (lhs.getPosX() - rhs.getPosX());
			}
		});
		
		Vec2[] groundLines = new Vec2[2 + waterEntities.size() * 4];
		int groundLineIndex = 0;
		
		// Initial Pos
		Vec2 pos = new Vec2(-10, 0);
		groundLines[groundLineIndex++] = pos;
		
		float waterWidth;
		float waterHeight;
		
		for (int i = 0 ; i < waterEntities.size() ; i++) {
			
			waterWidth = waterEntities.get(i).getWidth();
			waterHeight = waterEntities.get(i).getHeight();
			
			// Ground-Water Top Left
			pos = new Vec2(
					waterEntities.get(i).getPosX() - waterWidth / 2, 0);
			groundLines[groundLineIndex++] = pos;
			
			// Ground-Water Bottom Left
			pos = new Vec2(waterEntities.get(i).getPosX() - waterWidth / 2,
					waterEntities.get(i).getPosY() - waterHeight / 2);
			groundLines[groundLineIndex++] = pos;
			
			// Ground-Water Bottom Right
			pos = new Vec2(waterEntities.get(i).getPosX() + waterWidth / 2,
					waterEntities.get(i).getPosY() - waterHeight / 2);
			groundLines[groundLineIndex++] = pos;
			
			// Ground-Water Top Right
			pos = new Vec2(
					waterEntities.get(i).getPosX() + waterWidth / 2, 0);
			groundLines[groundLineIndex++] = pos;
			
		}
		
		pos = new Vec2(lastX + 10, 0);
		groundLines[groundLineIndex++] = pos;
		
		return groundLines;
	}
	
	@Override
	public void render(GLCanvas canvas, float timeElapsed) {
		//Render All bodies
		Body body = world.getBodyList();
		
		while (body != null) {
			
			Fixture fixture = body.getFixtureList();
			
			canvas.saveState();
			canvas.translatePhysics(body.getPosition().x, body.getPosition().y);
			canvas.rotate((float) (180 * - body.getAngle() / Math.PI));
			
			while (fixture != null) {
				
				switch (fixture.getShape().getType()) {
				
				case POLYGON:
					drawPolygon(canvas, (PolygonShape) fixture.getShape());
					break;
				case CIRCLE:
					drawCircle(canvas, (CircleShape) fixture.getShape());
					break;
					
				case CHAIN:
					canvas.restoreState(); // TODO: Remove this hack when we stop rotating OpenGL
					canvas.saveState(); // TODO: same as above
					drawChain(canvas, (ChainShape) fixture.getShape());
					break;
				case EDGE:
					drawEdge(canvas, (EdgeShape) fixture.getShape());
					break;
				}
				
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
		canvas.drawPhysicsLines(shape.getVertices(), shape.getVertexCount(), 3, Color.YELLOW, true);
		
	}
}
