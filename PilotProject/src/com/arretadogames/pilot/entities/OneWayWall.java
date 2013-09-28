package com.arretadogames.pilot.entities;

import java.util.HashSet;

import org.jbox2d.collision.Manifold;
import org.jbox2d.collision.WorldManifold;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.contacts.Contact;

import android.graphics.RectF;

import com.arretadogames.pilot.R;
import com.arretadogames.pilot.render.Sprite;
import com.arretadogames.pilot.render.opengl.GLCanvas;

public class OneWayWall extends Entity{

	private Fixture m_platformFixture;
	private float width;
	private float height;
	final private WorldManifold worldManifold = new WorldManifold();
	private HashSet<Contact> contactSet;
	private Sprite sprite;
	
	public OneWayWall(float x, float y) {
		super(x, y);
		PolygonShape shape = new PolygonShape();
		width = 3;
		height = 0.1f;
		shape.setAsBox(width/2, height/2);
		m_platformFixture = body.createFixture(shape,  1f);
		body.setType(BodyType.KINEMATIC);
		contactSet = new HashSet<Contact>();
	}

	public void beginContact(Entity e, Contact contact) {
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

		if ( platformFixture == null || otherFixture.isSensor()){
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
		if(numPoints == 0) return;
		contact.setEnabled(false);
		contactSet.add(contact);
	}

	public void endContact(Entity e, Contact contact) {
		contactSet.remove(contact);
	}

	public void preSolve(Contact contact, Manifold oldManifold){
		if(contactSet.contains(contact))contact.setEnabled(false);
	}

	@Override
	public void render(GLCanvas canvas, float timeElapsed) {
		
		canvas.saveState();
		canvas.translatePhysics(getPosX(), getPosY());
		canvas.rotate((float) (180 * - body.getAngle() / Math.PI));
		RectF rect = new RectF(
				(- width/2 * GLCanvas.physicsRatio), // Top Left X
				((- height/2 - 0.3f) * GLCanvas.physicsRatio), // Top Left Y
				(width/2 * GLCanvas.physicsRatio), // Bottom Right X
				((height/2 + 2) * GLCanvas.physicsRatio)); // Bottom Right Y

//		canvas.drawRect((int) rect.left, (int) rect.top, (int) rect.right, (int) rect.bottom, Color.WHITE);
		canvas.drawBitmap(sprite.getCurrentFrame(timeElapsed), rect, false);
		canvas.restoreState();
	}

	@Override
	public EntityType getType() {
		return EntityType.ONEWAY_WALL;
	}

	@Override
	public void setSprite(Sprite sprite) {
		this.sprite = sprite;
	}

	public int[] getStoppedFrames() {
		return new int[] {R.drawable.forest_platform};
	}

	public float[] getStoppedFramesDuration() {
		return new float[] {-1f};
	}
}
