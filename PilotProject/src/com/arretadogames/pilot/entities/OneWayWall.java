package com.arretadogames.pilot.entities;

import org.jbox2d.collision.Manifold;
import org.jbox2d.collision.WorldManifold;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.contacts.Contact;

import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.RectF;

import com.arretadogames.pilot.render.Sprite;
import com.arretadogames.pilot.render.opengl.GLCanvas;

public class OneWayWall extends Entity{

	private boolean flagC;
	private Fixture m_platformFixture;
	private float width;
	private float height;
	final private WorldManifold worldManifold = new WorldManifold();
	
	public OneWayWall(float x, float y) {
		super(x, y);
		PolygonShape shape = new PolygonShape();
		width = 3;
		height = 0.1f;
		shape.setAsBox(width/2, height/2);
		m_platformFixture = body.createFixture(shape,  1f);
		body.setType(BodyType.KINEMATIC);
	}

	public void beginContact(Entity e, Contact contact) {
		System.out.println("comeco");
		Fixture fixtureA = contact.getFixtureA();
		Fixture fixtureB = contact.getFixtureB();

		Fixture platformFixture = null;
		Fixture otherFixture = null;
		if ( fixtureA == m_platformFixture ) {
			platformFixture = fixtureA;
			otherFixture = fixtureB;
		}
		else if ( fixtureB == m_platformFixture ) {
			platformFixture = fixtureB;
			otherFixture = fixtureA;
		}

		if ( platformFixture == null){
			return;
		}

		Body platformBody = platformFixture.getBody();
		Body otherBody = otherFixture.getBody();

		int numPoints = contact.getManifold().pointCount;
		
		contact.getWorldManifold( worldManifold );

		for (int i = 0; i < numPoints; i++) {
		      Vec2 pointVelPlatform = platformBody.getLinearVelocityFromWorldPoint( worldManifold.points[i] );
		      Vec2 pointVelOther = otherBody.getLinearVelocityFromWorldPoint( worldManifold.points[i] );
		      Vec2 relativeVel = platformBody.getLocalVector( pointVelOther.add(pointVelPlatform.negate() ) );
		      
		      if ( relativeVel.y < -1 ) 
		          return;
		      else if ( relativeVel.y < 1 ) {
		          Vec2 relativePoint = platformBody.getLocalPoint( worldManifold.points[i] );
		          float platformFaceY = height;//front of platform  :(
		          if ( relativePoint.y > platformFaceY - 0.05 )
		              return;
		      }
		}
		contact.setEnabled(false);
		flagC = false;
	}

	public void endContact(Entity e, Contact contact) {
		flagC = true;
	}

	public void preSolve(Contact contact, Manifold oldManifold){
		if( flagC == false ) contact.setEnabled(false);
	}

	@Override
	public void render(GLCanvas canvas, float timeElapsed) {
		canvas.saveState();
		canvas.translatePhysics(getPosX(), getPosY());
		canvas.rotate((float) (180 * - body.getAngle() / Math.PI));
		RectF rect = new RectF(
				(- width/2 * GLCanvas.physicsRatio), // Top Left
				(- height/2 * GLCanvas.physicsRatio), // Top Left
				(width/2 * GLCanvas.physicsRatio), // Bottom Right
				(height/2 * GLCanvas.physicsRatio)); // Bottom Right
		
		canvas.drawRect((int) rect.left, (int) rect.top, (int) rect.right, (int) rect.bottom, Color.WHITE);
		canvas.restoreState();
	}

	@Override
	public EntityType getType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setSprite(Sprite sprite) {
		// TODO Auto-generated method stub
		
	}
}
