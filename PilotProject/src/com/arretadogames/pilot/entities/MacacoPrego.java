package com.arretadogames.pilot.entities;



import java.util.Collection;
import java.util.HashSet;

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.Filter;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.contacts.Contact;
import org.jbox2d.dynamics.joints.RevoluteJointDef;

import com.arretadogames.pilot.R;
import com.arretadogames.pilot.config.GameSettings;
import com.arretadogames.pilot.items.SuperJump;
import com.arretadogames.pilot.items.SuperStrength;
import com.arretadogames.pilot.render.PhysicsRect;
import com.arretadogames.pilot.render.Sprite;
import com.arretadogames.pilot.render.opengl.GLCanvas;

public class MacacoPrego extends Player implements Steppable{

	private Sprite sprite;
	private int contJump;
	private int contAct;
	private Fixture footFixture;
	Collection<Body> bodiesContact;
	private float radius = 0.3f;
	private boolean isonliana;
	private Body b;
	private int doubleJump;
	
	private static final int[] WALKING = {R.drawable.monkey_walk_right_1, R.drawable.monkey_walk_right_2};
	private static final int[] JUMP = {R.drawable.monkey_jump_right, R.drawable.monkey_jump_right_1, R.drawable.monkey_jump_right_2, R.drawable.monkey_jump_right_3};
	
	/*private static final int[] ACT = {R.drawable.lobo_guara_act1,
				 R.drawable.lobo_guara_act2,
				 R.drawable.lobo_guara_act3,
				 };*/
	
	public MacacoPrego(float x, float y, PlayerNumber number) {
		super(x, y, number);
		applyConstants();
		addItem(new SuperStrength(5));
		doubleJump = getMaxDoubleJumps();
		//PolygonShape shape = new PolygonShape();
		//shape.setAsBox(0.5f, 0.5f); // FIXME Check this size
		CircleShape shape = new CircleShape();
		shape.setRadius(radius );
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
		footShape.setAsBox(radius, 0.1f, new Vec2(0f,-radius+0.1f), 0f);
		footFixture = body.createFixture(footShape, 0f);
		footFixture.setSensor(true);
		
		footFixture.setFilterData(filter);
		
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
		b.createFixture(fd).setFilterData(filter);;
		b.setUserData(this);
		RevoluteJointDef jd2 = new RevoluteJointDef();
		jd2.bodyA = body;
		jd2.bodyB = b;
		jd2.collideConnected = false;
		jd2.localAnchorA.set(new Vec2(0f,0.2f));
		
		jd2.localAnchorB.set(new Vec2(0f,0.1f));
		world.createJoint(jd2);
		
		physRect = new PhysicsRect(0.5f, 0.6f);
	}
	
	private void applyConstants() {
		setMaxJumpVelocity(GameSettings.MACACO_MAX_JUMP_VELOCITY);
		setMaxRunVelocity(GameSettings.MACACO_MAX_RUN_VELOCITY);
		setJumpAceleration(GameSettings.MACACO_JUMP_ACELERATION);
		setRunAceleration(GameSettings.MACACO_RUN_ACELERATION);
		setTimeWaitingForAct(GameSettings.MACACO_TIME_WAITING_FOR_ACT);
		setMaxDoubleJumps(0);
	}
	
	@Override
	public PolygonShape getWaterContactShape() {
		PolygonShape a = new PolygonShape();
		a.setAsBox(radius, radius);
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
	
	public void setOnLiana(boolean bo){
		isonliana = bo;
		b.setFixedRotation(!bo);
	}

	public void jump() {
		if (hasFinished() || !isAlive() || contJump > 0 || (bodiesContact.size() <= 0 && doubleJump == 0))
			return;
		if(bodiesContact.size() <= 0 && doubleJump > 0){
			doubleJump--;
		} else {
			doubleJump = getMaxDoubleJumps();
		}
		sprite.setAnimationState("jump");
		float impulseX = (getJumpAceleration()-body.getLinearVelocity().y) * body.getMass();
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
		if( isonliana) {
			sprite.setAnimationState("jump");
			return;
		}
		if(body.getLinearVelocity().x < 1.5){ 
			body.applyLinearImpulse(new Vec2(1 * body.getMass(),0f), body.getWorldCenter());
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
		if( bodiesContact.size() > 0 && contAct == 0){
			
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
		}
		if(contJump > 0) contJump--;
		if(contAct > 0 ) contAct--;
		run();
	}
	
	public void beginContact(Entity e, Contact contact) {
		if( (contact.m_fixtureA.equals(footFixture) &&(!contact.m_fixtureB.isSensor() || e.getType() == EntityType.FLUID))|| (contact.m_fixtureB.equals(footFixture) &&(!contact.m_fixtureA.isSensor() || e.getType() == EntityType.FLUID)) ){
			bodiesContact.add(e.body);
			sprite.setAnimationState("walking");
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
		return new float[] {0.5f, 0.5f};
	}

	@Override
	public int[] getJumpFrames() {
		return JUMP;
	}
	
	public float[] getJumpFramesDuration(){
		return new float[] {1f, 1f, 0f};
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
		canvas.drawBitmap(sprite.getCurrentFrame(timeElapsed), physRect);
		canvas.restoreState();
		
	}

	public Body getContactLianaBody() {
		return b;
	}
	
	@Override
	public int getStatusImg() {
		return R.drawable.macaco_status;
	}
}
