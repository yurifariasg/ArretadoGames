package com.arretadogames.pilot.entities;



import java.util.Collection;
import java.util.Date;
import java.util.HashSet;

import org.jbox2d.collision.shapes.CircleShape;
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

public class TatuBola extends Player implements Steppable{

	private Sprite sprite;
	private int contJump;
	private int contAct;
	private int contacts;
	private Fixture footFixture;
	private final float MAX_JUMP_VELOCITY = 5;
	private final float MAX_RUN_VELOCITY = 4;
	private float JUMP_ACELERATION = 4;
	private float RUN_ACELERATION = 4;
	Collection<Body> bodiesContact;
	Date lastAct;
	private final float TIME_WAITING_FOR_ACT = 3000;
	private float timeForNextAct = 0f;
	
	private static final int[] WALKING = {R.drawable.tatu1,
										  R.drawable.tatu2,
										  R.drawable.tatu3,
										  R.drawable.tatu0};

	private static final int[] JUMP = {R.drawable.tatu_rowling1,
									  R.drawable.tatu_rowling2,
									  R.drawable.tatu_rowling3,
									  R.drawable.tatu_rowling4,
									  R.drawable.tatu_rowling5};
	
	private static final int[] ACT = {R.drawable.tatu_rowling1,
									  R.drawable.tatu_rowling2,
									  R.drawable.tatu_rowling3,
									  R.drawable.tatu_rowling4,
									  R.drawable.tatu_rowling5};
	
	private final float rad = 0.3f;
	public TatuBola(float x, float y, PlayerNumber number) {
		super(x, y, number);
		//PolygonShape shape = new PolygonShape();
		//shape.setAsBox(0.5f, 0.5f); // FIXME Check this size
		CircleShape shape = new CircleShape();
		shape.setRadius(rad);
		footFixture = body.createFixture(shape,  3f);
		footFixture.setFriction(0f);
		body.setType(BodyType.DYNAMIC);
		contJump = 0;
		contacts = 0;
		body.setFixedRotation(true);
		PolygonShape footShape = new PolygonShape();
		footShape.setAsBox(rad, 0.1f, new Vec2(0f,-rad + 0.1f), 0f);
		footFixture = body.createFixture(footShape, 0f);
		footFixture.setSensor(true);
		
		bodiesContact = new HashSet<Body>();
	}
	@Override
	public PolygonShape getWaterContactShape() {
		PolygonShape a = new PolygonShape();
		a.setAsBox(rad, rad);
		return a;
	}
	
	@Override
	public int getPercentageLeftToNextAct() {
		return Math.min((int)(((timeForNextAct/TIME_WAITING_FOR_ACT) * 100) + 0.000000001),100);
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
	
	

	public void jump() {
		if (hasFinished() || !isAlive() || contJump > 0 || contacts <= 0)
			return;
		
			sprite.setAnimationState("jump");
			float impulseX = Math.max(Math.min(JUMP_ACELERATION,(MAX_JUMP_VELOCITY - body.getLinearVelocity().y)) * body.getMass(),0);
			Vec2 direction = new Vec2(0,6);
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
		if(body.getLinearVelocity().x < 1.5){ 
			body.applyLinearImpulse(new Vec2(1 * body.getMass(),0f), body.getWorldCenter());
		}
		if(body.getLinearVelocity().length() > 8){
			Vec2 vel = body.getLinearVelocity();
			vel.normalize();
			body.setLinearVelocity(vel.mul(8));
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
			if( timeForNextAct < 0.00000001 ){
			timeForNextAct = TIME_WAITING_FOR_ACT;	
			sprite.setAnimationState("act");
			float impulse = (4) * body.getMass();
			//Vec2 direction = new Vec2((float)Math.cos(body.getAngle() ),(float)Math.sin(body.getAngle()));
			Vec2 direction = new Vec2(1,0);
			direction.normalize();
			direction.mulLocal(impulse);
			body.applyLinearImpulse(direction, body.getWorldCenter());
			contAct = 50;
			lastAct = new Date();
		}
		}
	}

	@Override
	public void step(float timeElapsed) {
		timeForNextAct = Math.max(0.0f,timeForNextAct-timeElapsed);
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
		
		Date t = new Date();
		if( contacts > 0 && !actActive && (lastAct == null || (t.getTime() - lastAct.getTime())/1000 > 3  )){
			sprite.setAnimationState("walking");
		}
		
		if(contJump > 0) contJump--;
		if(contAct > 0 ) contAct--;
		run();
	}
	
	public void beginContact(Entity e, Contact contact) {
		if(contact.m_fixtureA.equals(footFixture) || contact.m_fixtureB.equals(footFixture)){
			Date t = new Date();
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
		return new float[] {0.15f, 0.15f, 0.15f, 0.15f, 0.15f};
	}

	@Override
	public int[] getActFrames() {
		return ACT;
	}
	
	public float[] getActFramesDuration(){
		return new float[] {0.15f, 0.15f, 0.15f, 0.15f, 0.15f};
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
				(- (rad + 0.25f)* GLCanvas.physicsRatio), // Top Left
				(- (rad + 0.25f) * GLCanvas.physicsRatio), // Top Top Left
				((rad + 0.0f) * GLCanvas.physicsRatio), // Bottom Right
				((rad + 0.0f) * GLCanvas.physicsRatio)); // Bottom Right
		
		
		canvas.drawBitmap(sprite.getCurrentFrame(timeElapsed), rect, false);
		canvas.restoreState();
		
	}
}
