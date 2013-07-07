package com.arretadogames.pilot.entities;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.joints.Joint;
import org.jbox2d.dynamics.joints.JointDef;
import org.jbox2d.dynamics.joints.RevoluteJointDef;

import android.graphics.Color;
import android.graphics.RectF;
import android.util.Pair;

import com.arretadogames.pilot.render.Sprite;
import com.arretadogames.pilot.render.opengl.GLCanvas;

public class Liana extends Entity implements Steppable{

	private ArrayList<LianaNode> elements;
	private Collection<Player> conectedPlayers;
	private Collection<Joint> joints;
	Collection< Pair<Player, LianaNode> > toCreateJoints;
	private final float HEIGHT = 0.4f;
	private final float WIDTH = 0.1f;
	private final float GAP = 0.05f;
	private Joint joint;

	private Liana(float x, float y) {
		super(x, y);
		conectedPlayers = new HashSet<Player>();
		joints = new ArrayList<Joint>();
		elements = new ArrayList<LianaNode>();
		toCreateJoints = new ArrayList<Pair<Player,LianaNode>>();

		PolygonShape sini = new PolygonShape();
		sini.setAsBox(0.1f,0.1f);
		body.createFixture(sini,0f).setSensor(true);
		body.setType(BodyType.STATIC);
	}
	
	/**
	 * 
	 * @param x x coordinate of the top end
	 * @param y y coordinate of the top end
	 * @param xf x coordinate of the bottom end
	 * @param yf y coordinate of the bottom end
	 * @param size 
	 */
	public Liana(float x, float y, float xf, float yf, float size) {
		this(x, y);
		createChain(x, y, xf, yf, size);
	}
	
	public Liana(float x, float y, float xf, float yf) {
		this(x, y);
		Vec2 ini = new Vec2(x,y);
		Vec2 fin = new Vec2(xf,yf);
		float size = (ini.sub(fin)).length() + 1*HEIGHT;
		createChain(x, y, xf, yf, size);
	}
	
	private void createChain(float x, float y, float xf, float yf, float size){
		
		RevoluteJointDef jd = new RevoluteJointDef();
		jd.collideConnected = false;

		Body prevBody = body;
		int quant = (int)(size/(HEIGHT-2*GAP));
		for (int i = 0; i < quant; ++i){
			LianaNode ln = new LianaNode(x, y -(HEIGHT-2*GAP)/2 - i*(HEIGHT-2*GAP), WIDTH, HEIGHT,this);
			Vec2 anchor = new Vec2(x, y - i*(HEIGHT-2*GAP));
			jd.initialize(prevBody, ln.body, anchor);
			world.createJoint(jd);
			elements.add(ln);
			prevBody = ln.body;
		}
		
		BodyDef bd = new BodyDef();
		bd.position.set(xf ,yf);
		Body body2 = world.createBody(bd);
		body2.setSleepingAllowed(true);
		body2.setAwake(false);
		body2.setType(BodyType.STATIC);
		
//		PolygonShape sini = new PolygonShape();
//		sini.setAsBox(0.1f,0.1f);
//		body2.createFixture(sini,0.1f);
		
		RevoluteJointDef jd2 = new RevoluteJointDef();
		jd2.bodyA = body2;
		jd2.bodyB = prevBody;
		jd2.collideConnected = false;
		jd2.localAnchorA.set(new Vec2());
		jd2.localAnchorB.set(new Vec2());
		joint = world.createJoint(jd2);
	}



	@Override
	public void step(float timeElapsed) {
		if(joints.isEmpty()){
			for(LianaNode ln : elements){
//				ln.step(timeElapsed);
			}
		} else if( joint != null){
			world.destroyJoint(joint);
			joint = null;
		}
		createJoints();	
		removeJoints();
	}



	private void createJoints() {
		for(Pair<Player,LianaNode> pair : toCreateJoints){
			Player player = pair.first;
			LianaNode node = pair.second;
			if(!conectedPlayers.contains(player)){
				conectedPlayers.add(player);
				createJoint(player, node);
			}
		}

		toCreateJoints.clear();
	}



	private void removeJoints() {
		Collection<Player> toLeave = new ArrayList<Player>();
		for(Player player : conectedPlayers){
			if(!player.actActive){
				toLeave.add(player);

				Collection<Joint> toDestroy = new ArrayList<Joint>();
				for(Joint joint : joints){
					if(((Player)joint.getBodyA().getUserData()).equals(player)){
						toDestroy.add(joint);
						world.destroyJoint(joint);
					}
				}
				for(Joint joint : toDestroy){
					joints.remove(joint);
				}
			}
		}
		for(Player player : toLeave){
			conectedPlayers.remove(player);
		}
	}



	private void createJoint(Player player, LianaNode node) {
		RevoluteJointDef jd = new RevoluteJointDef();
		jd.bodyA = player.body;
		jd.bodyB = node.body;
		jd.localAnchorA.set(new Vec2());
		jd.localAnchorB.set(new Vec2());
		jd.collideConnected = false;
		joints.add(world.createJoint(jd));
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

	synchronized public void playerContact(Entity e, LianaNode l) {
		toCreateJoints.add(new Pair<Player,LianaNode>((Player)e,l));
	}
}
