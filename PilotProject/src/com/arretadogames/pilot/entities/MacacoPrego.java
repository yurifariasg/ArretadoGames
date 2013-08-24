package com.arretadogames.pilot.entities;



import java.util.Collection;
import java.util.HashSet;

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.contacts.Contact;
import org.jbox2d.dynamics.joints.RevoluteJointDef;

import android.graphics.Color;
import android.graphics.RectF;

import com.arretadogames.pilot.R;
import com.arretadogames.pilot.render.Sprite;
import com.arretadogames.pilot.render.opengl.GLCanvas;

public class MacacoPrego extends Player implements Steppable{

	private Sprite sprite;
	private int contJump;
	private int contAct;
	private int contacts;
	private Fixture footFixture;
	private final float MAX_JUMP_VELOCITY = 8;
	private final float MAX_RUN_VELOCITY = 3;
	private float JUMP_ACELERATION = 6;
	private float RUN_ACELERATION = 6;
	Collection<Body> bodiesContact;
	private float radius = 0.3f;
	private boolean isonliana;
	private Body b;
	
	private static final int[] WALKING = {R.drawable.macacoandando};

	private static final int[] JUMP = {R.drawable.macacoandando};
	
	/*private static final int[] ACT = {R.drawable.lobo_guara_act1,
				 R.drawable.lobo_guara_act2,
				 R.drawable.lobo_guara_act3,
				 };*/
	
	public MacacoPrego(float x, float y, PlayerNumber number) {
		super(x, y, number);
		//PolygonShape shape = new PolygonShape();
		//shape.setAsBox(0.5f, 0.5f); // FIXME Check this size
		CircleShape shape = new CircleShape();
		shape.setRadius(radius );
		footFixture = body.createFixture(shape,  3f);
		footFixture.setFriction(0f);
		body.setType(BodyType.DYNAMIC);
		contJump = 0;
		contacts = 0;
		body.setFixedRotation(true);
		PolygonShape footShape = new PolygonShape();
		footShape.setAsBox(radius, 0.1f, new Vec2(0f,-radius+0.1f), 0f);
		footFixture = body.createFixture(footShape, 0f);
		footFixture.setSensor(true);
		bodiesContact = new HashSet<Body>();
		isonliana = false;
		
		PolygonShape shape2 = new PolygonShape();
		shape2.setAsBox(0.05f, 0.2f);

		FixtureDef fd = new FixtureDef();
		fd.shape = shape2;
		fd.density = 3.0f;
		fd.friction = 0.2f;
		BodyDef bf = new BodyDef();
		bf.type = BodyType.DYNAMIC;
		b = world.createBody(bf);
		b.createFixture(fd);
		b.setUserData(this);
		RevoluteJointDef jd2 = new RevoluteJointDef();
		jd2.bodyA = body;
		jd2.bodyB = b;
		jd2.collideConnected = false;
		jd2.localAnchorA.set(new Vec2(0f,0.2f));
		
		jd2.localAnchorB.set(new Vec2(0f,0.1f));
		world.createJoint(jd2);
	}

	double getAngle(){
		//return body.getAngle();
		double angle = 0;
		if(body.getLinearVelocity().length() > 1){
			double cos = Vec2.dot(body.getLinearVelocity(), new Vec2(1,0)) / (body.getLinearVelocity().length());
			cos = Math.abs(cos);
			angle = Math.acos(cos);
			//System.out.println(cos + " - " + angle);
			if( body.getLinearVelocity().y < 0 ) angle = angle * -1;
			angle = Math.min(Math.PI/6,angle);
			angle = Math.max(-Math.PI/6,angle);
		}
		return angle;
	}
	
	public void setOnLiana(boolean bo){
		isonliana = bo;
		b.setFixedRotation(!bo);
	}

	public void jump() {
		if (hasFinished() || !isAlive() || contJump > 0 || contacts <= 0)
			return;
		sprite.setAnimationState("jump");
		float impulseX = Math.max(Math.min(JUMP_ACELERATION,(MAX_JUMP_VELOCITY - body.getLinearVelocity().y)) * body.getMass(),0);
		Vec2 direction = new Vec2(1,6);
		direction.normalize();
		direction.mulLocal(impulseX);
		body.applyLinearImpulse(direction, body.getWorldCenter());
		contJump = 5;
		applyReturn(direction);
	}
	
	private void applyReturn(Vec2 impulse){
		int quant = bodiesContact.size() * 3;
		for(Body b : bodiesContact){
			b.applyLinearImpulse(impulse.mul(-1/quant), b.getWorldCenter());
			b.applyLinearImpulse(impulse.mul(-1/quant), body.getWorldPoint(new Vec2(-0.5f,-0.6f)));
			b.applyLinearImpulse(impulse.mul(-1/quant), body.getWorldPoint(new Vec2(0.5f,-0.6f)));
		}
	}
	
	
	public void run(){
		if( isonliana)return;
		if(body.getLinearVelocity().x < 1.5){ 
			body.applyLinearImpulse(new Vec2(1 * body.getMass(),0f), body.getWorldCenter());
		}
		if(contacts > 0 && body.getLinearVelocity().x < MAX_RUN_VELOCITY){
			float force = (RUN_ACELERATION) * body.getMass();
			//Vec2 direction = new Vec2((float)Math.cos(body.getAngle() ),(float)Math.sin(body.getAngle()));
			Vec2 direction = new Vec2(1,0);
			direction.normalize();
			direction.mulLocal(force);
			body.applyForceToCenter(direction);
		}
	}

	public void act() {	
		if( contacts > 0 && contAct == 0){
			
		}
	}

	@Override
	public void step(float timeElapsed) {
		if (hasFinished() || !isAlive()) {
			return;
		}
		if (jumpActive) {
			jump();
			jumpActive = false;
		}
		if(actActive){
			act();
		}
		if(contJump > 0) contJump--;
		if(contAct > 0 ) contAct--;
		run();
	}
	
	public void beginContact(Entity e, Contact contact) {
		if(contact.m_fixtureA.equals(footFixture) || contact.m_fixtureB.equals(footFixture)){
			sprite.setAnimationState("walking");
			contacts++;
			bodiesContact.add(e.body);
		}
	}

	public void endContact(Entity e , Contact contact) {
		if(contact.m_fixtureA.equals(footFixture) || contact.m_fixtureB.equals(footFixture)){
			contacts--;
			bodiesContact.remove(e.body);
		}
		if(contacts==0){
			sprite.setAnimationState("jump");
		}
	}

	@Override
	public int[] getWalkFrames() {
		return WALKING;
	}
	
	public float[] getWalkFramesDuration(){
		return new float[] {0.15f, 0.15f, 0.15f, 0.15f, 0.15f ,0.15f};
	}

	@Override
	public int[] getJumpFrames() {
		return JUMP;
	}
	
	public float[] getJumpFramesDuration(){
		return new float[] {0.3f, 0.3f, 0f};
	}

	@Override
	public int[] getActFrames() {
		return null;
		//TODO
/*		Bitmap[] frames = new Bitmap[ACT.length];
		for (int i = 0; i < ACT.length; i++) {
			frames[i] = ImageLoader.loadImage(ACT[i]);
		}
		return frames;*/
	}
	
	public float[] getActFramesDuration(){
		return new float[] {0.15f, 0.15f};
	}
	
	public void setSprite(Sprite sprite){
		this.sprite = sprite;
	}
	
	@Override
	public void render(GLCanvas canvas, float timeElapsed) {
		
		
		
		canvas.saveState();
		canvas.translatePhysics(getPosX(), getPosY());
		canvas.rotate((float) (180 * - getAngle() / Math.PI)); // getAngle() ou body.getAngle() ?
		
		RectF rect = new RectF(
				(- radius* GLCanvas.physicsRatio), // Top Left
				(- radius * GLCanvas.physicsRatio), // Top Top Left
				(radius * GLCanvas.physicsRatio), // Bottom Right
				(radius * GLCanvas.physicsRatio)); // Bottom Right
		
		canvas.drawBitmap(sprite.getCurrentFrame(timeElapsed), rect, false);
		canvas.restoreState();
		
		canvas.saveState();
		canvas.translatePhysics(b.getPosition().x, b.getPosition().y);
		canvas.rotate((float) (180 * - b.getAngle() / Math.PI)); // getAngle() ou body.getAngle() ?
		RectF rect2 = new RectF(
				(- 0.1f* GLCanvas.physicsRatio), // Top Left
				(- 0.3f * GLCanvas.physicsRatio), // Top Top Left
				(0.1f * GLCanvas.physicsRatio), // Bottom Right
				(0.3f * GLCanvas.physicsRatio)); // Bottom Right
		
		canvas.drawRect((int) rect2.left, (int) rect2.top, (int) rect2.right, (int) rect2.bottom, Color.YELLOW);
		canvas.restoreState();
		
	}

	public Body getContactLianaBody() {
		return b;
	}
}
