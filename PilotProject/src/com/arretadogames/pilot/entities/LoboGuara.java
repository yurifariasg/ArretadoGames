package com.arretadogames.pilot.entities;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.Filter;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.contacts.Contact;

import com.arretadogames.pilot.R;
import com.arretadogames.pilot.render.PhysicsRect;
import com.arretadogames.pilot.render.Sprite;
import com.arretadogames.pilot.render.opengl.GLCanvas;

public class LoboGuara extends Player implements Steppable{

	private Sprite sprite;
	private int contJump;
	private int contAct;
	private Fixture footFixture;
	private final float MAX_JUMP_VELOCITY = 5;
	private final float MAX_RUN_VELOCITY = 3;
	private float JUMP_ACELERATION = 3;
	private float RUN_ACELERATION = 5;
	Collection<Body> bodiesContact;
	Date lastAct;
	private float size;
	private final float TIME_WAITING_FOR_ACT = 6;
	private float timeForNextAct = 0f;
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
	
	public LoboGuara(float x, float y, PlayerNumber number) {
		super(x, y, number);
		//PolygonShape shape = new PolygonShape();
		//shape.setAsBox(0.5f, 0.5f); // FIXME Check this size
		CircleShape shape = new CircleShape();
		size = 0.5f;
		shape.setRadius(size);
		footFixture = body.createFixture(shape,  3f);
		footFixture.setFriction(0f);
		
		Filter filter = new Filter();
		filter.categoryBits = CollisionFlag.GROUP_1.getValue() ;
		filter.maskBits = CollisionFlag.GROUP_1.getValue() ;
		footFixture.setFilterData(filter);
		
		body.setType(BodyType.DYNAMIC);
		contJump = 0;
		body.setFixedRotation(true);
		PolygonShape footShape = new PolygonShape();
		footShape.setAsBox(0.5f, 0.1f, new Vec2(0f,-0.4f), 0f);
		footFixture = body.createFixture(footShape, 0f);
		footFixture.setSensor(true);
		footFixture.setFilterData(filter);
		bodiesContact = new HashSet<Body>();
		
		physRect = new PhysicsRect(1.4f, 1.6f);
	}
	
	@Override
	public PolygonShape getWaterContactShape() {
		PolygonShape a = new PolygonShape();
		a.setAsBox(size, size);
		return a;
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
		if (hasFinished() || !isAlive() || contJump > 0 || bodiesContact.size() <= 0)
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
		if(body.getLinearVelocity().x < 0.5){ 
			body.applyLinearImpulse(new Vec2(0.5f * body.getMass(),0f), body.getWorldCenter());
		}
		if(body.getLinearVelocity().length() > 8){
			Vec2 vel = body.getLinearVelocity().clone();
			vel.normalize();
			body.setLinearVelocity(vel.mul(8));
		}
		if(bodiesContact.size() > 0 && body.getLinearVelocity().x < MAX_RUN_VELOCITY){
			float force = (RUN_ACELERATION) * body.getMass();
			//Vec2 direction = new Vec2((float)Math.cos(body.getAngle() ),(float)Math.sin(body.getAngle()));
			Vec2 direction = new Vec2(1,0);
			direction.normalize();
			direction.mulLocal(force);
			body.applyForceToCenter(direction);
		}
	}

	public void act() {	
		if( bodiesContact.size() > 0 && contAct == 0){
			if( timeForNextAct < 0.00000001 ){
			timeForNextAct = TIME_WAITING_FOR_ACT;
			float impulse = (3) * body.getMass();
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
	public int getPercentageLeftToNextAct() {
		return Math.min((int)((((TIME_WAITING_FOR_ACT-timeForNextAct)/TIME_WAITING_FOR_ACT) * 100) + 0.000000001),100);
	}
	
	
	
	@Override
	public void step(float timeElapsed) {
		timeForNextAct = Math.max(0.0f,timeForNextAct-timeElapsed);
		if (hasFinished() || !isAlive()) {
			if (hasFinished())
				stopAction();
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
		if( (contact.m_fixtureA.equals(footFixture) &&(!contact.m_fixtureB.isSensor() || e.getType() == EntityType.FLUID))|| (contact.m_fixtureB.equals(footFixture) &&(!contact.m_fixtureA.isSensor() || e.getType() == EntityType.FLUID)) ){
			bodiesContact.add(e.body);
		}
	}

	public void endContact(Entity e , Contact contact) {
		if(contact.m_fixtureA.equals(footFixture) || contact.m_fixtureB.equals(footFixture)){
			if(bodiesContact.contains(e.body)){
				bodiesContact.remove(e.body);
				
			}
			
		}
		if(bodiesContact.size()==0){
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
		canvas.translatePhysics(getPosX(), getPosY() + 0.3f);
		canvas.rotate((float) (180 * - getAngle() / Math.PI)); // getAngle() ou body.getAngle() ?
		canvas.drawBitmap(sprite.getCurrentFrame(timeElapsed), physRect);
		canvas.restoreState();
	}
	
	@Override
	public int getStatusImg() {
		return R.drawable.lobo_status;
	}
}
