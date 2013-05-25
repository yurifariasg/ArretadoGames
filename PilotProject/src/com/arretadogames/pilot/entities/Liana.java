package com.arretadogames.pilot.entities;


import java.util.ArrayList;
import java.util.Collection;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.joints.JointDef;
import org.jbox2d.dynamics.joints.RevoluteJointDef;

import android.graphics.Color;
import android.graphics.RectF;

import com.arretadogames.pilot.render.Sprite;
import com.arretadogames.pilot.render.opengl.GLCanvas;

public class Liana extends Entity {

	private ArrayList<LianaNode> elements;
	private Entity player;
	private LianaNode node;
	private boolean foi;
	public Liana(float x, float y) {
		super(x, y);
		foi = false;
		node = null;
		elements = new ArrayList<LianaNode>();
		PolygonShape sini = new PolygonShape();
		sini.setAsBox(0.1f,0.1f);
		body.createFixture(sini,0f);
		body.setType(BodyType.STATIC);
		
		RevoluteJointDef jd = new RevoluteJointDef();
		jd.collideConnected = false;
		
		Body prevBody = body;
		float height = 0.4f;
		float width = 0.1f;
		float gap = 0.05f;
		for (int i = 0; i < 8; ++i){
			LianaNode ln = new LianaNode(x, y -(height-2*gap)/2 - i*(height-2*gap), width, height,this);
//			ln.body.setUserData(this);
			Vec2 anchor = new Vec2(x, y - i*(height-2*gap));
			jd.initialize(prevBody, ln.body, anchor);
			world.createJoint(jd);
			elements.add(ln);
			prevBody = ln.body;
		}
	}

	

	@Override
	public void step(float timeElapsed) {
//		for(LianaNode ln : elements){
//			ln.step(timeElapsed);
//		}
		elements.get(elements.size()-1).step(timeElapsed);
		elements.get(elements.size()-2).step(timeElapsed);
		if(node!=null && ! foi){
			foi = true;
			RevoluteJointDef jd = new RevoluteJointDef();
			jd.bodyA = player.body;
			jd.bodyB = node.body;
			jd.localAnchorA.set(new Vec2());
			jd.localAnchorB.set(new Vec2());
			jd.collideConnected = false;
			world.createJoint(jd);
		}
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

	@Override
	public void render(GLCanvas canvas, float timeElapsed) {
		for(LianaNode ln : elements){
			ln.render(canvas, timeElapsed);
		}
		canvas.saveState();
		canvas.translatePhysics(getPosX(), getPosY());
		canvas.rotate((float) (180 * - body.getAngle() / Math.PI));
		RectF rect = new RectF(
				(- 0.1f * GLCanvas.physicsRatio), // Top Left
				(- 0.1f * GLCanvas.physicsRatio), // Top Left
				(0.1f * GLCanvas.physicsRatio), // Bottom Right
				(0.1f * GLCanvas.physicsRatio)); // Bottom Right
		
		canvas.drawRect((int) rect.left, (int) rect.top, (int) rect.right, (int) rect.bottom, Color.WHITE);
		canvas.restoreState();
		
	}
	
	@Override
	public void destroyBody() {
		super.destroyBody();
		for(LianaNode ln : elements){
			ln.destroyBody();
		}
	}



	public void playerContact(Entity e, LianaNode l) {
		if( node == null){
			this.player = e;
			this.node = l;
		}
	}
}
