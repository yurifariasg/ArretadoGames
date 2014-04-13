package com.arretadogames.pilot.entities;

import com.arretadogames.pilot.R;
import com.arretadogames.pilot.config.GameSettings;
import com.arretadogames.pilot.render.PhysicsRect;
import com.arretadogames.pilot.render.opengl.GLCanvas;

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.Filter;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.joints.RevoluteJointDef;

public class MacacoPrego extends Player implements Steppable{

	private float radius = 0.3f;
	private boolean isOnLiana;
	private Body b;
	private int doubleJump;
	
	public MacacoPrego(float x, float y, PlayerNumber number) {
		super(x, y, number);
		applyConstants();
		doubleJump = getMaxDoubleJumps();

		CircleShape shape = new CircleShape();
		shape.setRadius(radius );
		bodyFixture = body.createFixture(shape,  3f);
		bodyFixture.setFriction(0f);
		
		Filter filter = new Filter();
		filter.categoryBits = CollisionFlag.GROUP_1.getValue() ;
		filter.maskBits = CollisionFlag.GROUP_1.getValue() ;
		bodyFixture.setFilterData(filter);
		
		body.setType(BodyType.DYNAMIC);
		body.setFixedRotation(true);
		PolygonShape footShape = new PolygonShape();
		footShape.setAsBox(radius, 0.1f, new Vec2(0f,-radius+0.1f), 0f);
		footFixture = body.createFixture(footShape, 0f);
		footFixture.setSensor(true);
		
		footFixture.setFilterData(filter);
		
		isOnLiana = false;
		
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
	
	public void setOnLiana(boolean bo){
		isOnLiana = bo;
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
	
	public void run(){
		if( isOnLiana) {
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

	@Override
	public void step(float timeElapsed) {
		applyConstants();
		super.step(timeElapsed);
        if (shouldStop()) {
            stopAction();
            return;
        }
		if (jumpActive) {
			jump();
			jumpActive = false;
		}

		if(contJump > 0) contJump--;
		if(contAct > 0 ) contAct--;
		run();
	}
	
	@Override
	public void playerRender(GLCanvas canvas, float timeElapsed) {
		
		canvas.saveState();
		canvas.translatePhysics(getPosX(), getPosY());
		canvas.rotate((float) (180 * - getAngle() / Math.PI));
        sprite.render(canvas, physRect, timeElapsed);
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
