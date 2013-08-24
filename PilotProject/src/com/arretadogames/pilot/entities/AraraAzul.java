package com.arretadogames.pilot.entities;



import java.util.Collection;
import java.util.HashSet;

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.contacts.Contact;

import android.graphics.Color;
import android.graphics.RectF;

import com.arretadogames.pilot.R;
import com.arretadogames.pilot.render.Sprite;
import com.arretadogames.pilot.render.opengl.GLCanvas;

public class AraraAzul extends Player implements Steppable{

	private Sprite sprite;
	private int contJump;
	private int contacts;
	private Fixture footFixture;
	private final float MAX_JUMP_VELOCITY = 7;
	private final float MAX_RUN_VELOCITY = 4;
	private float JUMP_ACELERATION = 5;
	private float RUN_ACELERATION = 10;
	Collection<Body> bodiesContact;
	private float k = 25f;
	private boolean doubleJump;
	
	private static final int[] WALKING = {R.drawable.lobo_g_walking1,
								  		     R.drawable.lobo_g_walking2,
								  		     R.drawable.lobo_g_walking3,
								  		     R.drawable.lobo_g_walking4,
								  		     R.drawable.lobo_g_walking5,
								  		     R.drawable.lobo_g_walking6};

	private static final int[] JUMP = {R.drawable.lobo_g_jump1,
  		  						  		R.drawable.lobo_g_jump2,
  		  						  		R.drawable.lobo_g_jump4};
	
	/*private static final int[] ACT = {R.drawable.lobo_guara_act1,
				 R.drawable.lobo_guara_act2,
				 R.drawable.lobo_guara_act3,
				 };*/
	
	public AraraAzul(float x, float y, PlayerNumber number) {
		super(x, y, number);
		//PolygonShape shape = new PolygonShape();
		//shape.setAsBox(0.5f, 0.5f); // FIXME Check this size
		CircleShape shape = new CircleShape();
		shape.setRadius(0.3f);
		footFixture = body.createFixture(shape,  k);
		footFixture.setFriction(0f);
		body.setType(BodyType.DYNAMIC);
		contJump = 0;
		contacts = 0;
		body.setFixedRotation(true);
		PolygonShape footShape = new PolygonShape();
		footShape.setAsBox(0.3f, 0.1f, new Vec2(0f,-0.4f), 0f);
		footFixture = body.createFixture(footShape, 0f);
		footFixture.setSensor(true);
		
		bodiesContact = new HashSet<Body>();
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
		if (hasFinished() || !isAlive() || contJump > 0 || (contacts <= 0 && !doubleJump))
			return;
		if(contacts <= 0 && doubleJump){
			doubleJump = false;
		} else {
			doubleJump = true;
		}
		sprite.setAnimationState("jump");
//		Math.max(Math.min(,(MAX_JUMP_VELOCITY - body.getLinearVelocity().y)
		float impulseX = (JUMP_ACELERATION-body.getLinearVelocity().y) * body.getMass();
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
		if (hasFinished() || !isAlive())
			return;
		float vel = body.getLinearVelocity().y;
		Vec2 force = new Vec2(0,k * vel * vel);
		if( vel < 0 ){ //force.negateLocal();
		body.applyForce(force, body.getWorldCenter());
		System.out.println("acting");}
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
				(- 0.3f * GLCanvas.physicsRatio), // Top Left
				(- 0.3f * GLCanvas.physicsRatio), // Top Left
				(0.3f * GLCanvas.physicsRatio), // Bottom Right
				(0.3f * GLCanvas.physicsRatio)); // Bottom Right
		
		canvas.drawBitmap(R.drawable.ave, rect, false);
//		canvas.drawRect((int) rect.left, (int) rect.top, (int) rect.right, (int) rect.bottom, Color.BLACK);
		canvas.restoreState();
		
	}
}
