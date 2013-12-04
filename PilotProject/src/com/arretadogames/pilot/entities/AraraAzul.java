package com.arretadogames.pilot.entities;

import java.util.Collection;
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
import com.arretadogames.pilot.config.GameSettings;
import com.arretadogames.pilot.render.PhysicsRect;
import com.arretadogames.pilot.render.Sprite;
import com.arretadogames.pilot.render.opengl.GLCanvas;

public class AraraAzul extends Player implements Steppable{

	private Sprite sprite;
	private int contJump;
	private Fixture footFixture;
	Collection<Body> bodiesContact;
	private float k = 3f;
	private boolean doubleJump;
	private float radius;
	
	private static final int[] WALKING = {R.drawable.bird_1,
											R.drawable.bird_2,
											R.drawable.bird_3,
											R.drawable.bird_2,
											R.drawable.bird_1,
											R.drawable.bird_4,
											R.drawable.bird_5,
											R.drawable.bird_6,
											R.drawable.bird_5,
											R.drawable.bird_4};

	private static final int[] JUMP = {R.drawable.bird_6,
  		  						  		R.drawable.bird_4,
  		  						  		R.drawable.bird_3,
  		  						  		R.drawable.bird_4};
	
	/*private static final int[] ACT = {R.drawable.lobo_guara_act1,
				 R.drawable.lobo_guara_act2,
				 R.drawable.lobo_guara_act3,
				 };*/
	
	public AraraAzul(float x, float y, PlayerNumber number) {
		super(x, y, number);
		applyConstants();
		//PolygonShape shape = new PolygonShape();
		//shape.setAsBox(0.5f, 0.5f); // FIXME Check this size
		CircleShape shape = new CircleShape();
		radius = 0.3f;
		shape.setRadius(radius);
		footFixture = body.createFixture(shape,  k);
		footFixture.setFriction(0f);
		body.setType(BodyType.DYNAMIC);
		
		Filter filter = new Filter();
		filter.categoryBits = CollisionFlag.GROUP_1.getValue() ;
		filter.maskBits = CollisionFlag.GROUP_1.getValue() ;
		footFixture.setFilterData(filter);
		
		contJump = 0;
		body.setFixedRotation(true);
		PolygonShape footShape = new PolygonShape();
		footShape.setAsBox(0.3f, 0.1f, new Vec2(0f,-0.4f), 0f);
		footFixture = body.createFixture(footShape, 0f);
		footFixture.setSensor(true);
		
		footFixture.setFilterData(filter);
		
		bodiesContact = new HashSet<Body>();
		
		// Drawing Rect
		physRect = new PhysicsRect(1, 1);
	}
	
	private void applyConstants() {
		setMaxJumpVelocity(GameSettings.ARARA_MAX_JUMP_VELOCITY);
		setMaxRunVelocity(GameSettings.ARARA_MAX_RUN_VELOCITY);
		setJumpAceleration(GameSettings.ARARA_JUMP_ACELERATION);
		setRunAceleration(GameSettings.ARARA_RUN_ACELERATION);
		setTimeWaitingForAct(GameSettings.ARARA_TIME_WAITING_FOR_ACT);
	}

	@Override
	public PolygonShape getWaterContactShape() {
		PolygonShape a = new PolygonShape();
		a.setAsBox(radius,radius);
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
		
		if (hasFinished() || !isAlive() || contJump > 0 || (bodiesContact.size() <= 0 && !doubleJump))
			return;
		if(bodiesContact.size() <= 0 && doubleJump){
			doubleJump = false;
		} else {
			doubleJump = true;
		}
		sprite.setAnimationState("jump");
//		Math.max(Math.min(,(MAX_JUMP_VELOCITY - body.getLinearVelocity().y)
		float impulseX = (getJumpAceleration()-body.getLinearVelocity().y) * body.getMass();
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
		if(bodiesContact.size() > 0 && body.getLinearVelocity().x < getMaxRunVelocity()){
			float force = (getRunAceleration()) * body.getMass();
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
		float velx = body.getLinearVelocity().x;
		Vec2 force = new Vec2((k * vel * vel) / 3,k * vel * vel);
		if( vel < 0 ){ //force.negateLocal();
			body.applyForce(force, body.getWorldCenter());
		}
		Vec2 forcex = new Vec2(-(velx * velx)/6 ,0);
		if( velx > 0 ){ //force.negateLocal();
			body.applyForce(forcex, body.getWorldCenter());
		}
	}

	@Override
	public void step(float timeElapsed) {
		applyConstants();
		super.step(timeElapsed);
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
			sprite.setAnimationState("act");
		}
		if(contJump > 0) contJump--;
		run();
	}
	
	public void beginContact(Entity e, Contact contact) {
		if( (contact.m_fixtureA.equals(footFixture) &&(!contact.m_fixtureB.isSensor() || e.getType() == EntityType.FLUID))|| (contact.m_fixtureB.equals(footFixture) &&(!contact.m_fixtureA.isSensor() || e.getType() == EntityType.FLUID)) ){
			sprite.setAnimationState("walking");
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
		return new float[] {0.15f, 0.15f, 0.15f, 0.15f, 0.15f ,0.15f, 0.15f, 0.15f, 0.15f ,0.15f}; // 10
	}

	@Override
	public int[] getJumpFrames() {
		return JUMP;
	}
	
	public float[] getJumpFramesDuration(){ // 4
		return new float[] {0.3f, 0.3f, 0.3f, 0f};
	}

	@Override
	public int[] getActFrames() {
		return JUMP;
		//TODO
/*		Bitmap[] frames = new Bitmap[ACT.length];
		for (int i = 0; i < ACT.length; i++) {
			frames[i] = ImageLoader.loadImage(ACT[i]);
		}
		return frames;*/
	}
	
	public float[] getActFramesDuration(){
		return new float[] {0.3f, 0.3f, 0.3f, 0f};
	}
	
	public void setSprite(Sprite sprite){
		this.sprite = sprite;
	}
	
	@Override
	public void render(GLCanvas canvas, float timeElapsed) {
		
		canvas.saveState();
		canvas.translatePhysics(getPosX(), getPosY());
		canvas.rotate((float) (180 * - getAngle() / Math.PI)); // getAngle() ou body.getAngle() ?
		canvas.drawBitmap(sprite.getCurrentFrame(timeElapsed), physRect);
		canvas.restoreState();
		
	}
	
	@Override
	public int getStatusImg() {
		return R.drawable.arara_status;
	}
}
