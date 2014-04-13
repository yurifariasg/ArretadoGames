package com.arretadogames.pilot.entities;

import com.arretadogames.pilot.R;
import com.arretadogames.pilot.config.GameSettings;
import com.arretadogames.pilot.render.PhysicsRect;
import com.arretadogames.pilot.render.opengl.GLCanvas;

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.Filter;

public class AraraAzul extends Player implements Steppable {

	private float k = 3f;
	private int doubleJump;
	private float radius;
	
	public AraraAzul(float x, float y, PlayerNumber number) {
		super(x, y, number);
		applyConstants();
		doubleJump = getMaxDoubleJumps();

		CircleShape shape = new CircleShape();
		radius = 0.3f;
		shape.setRadius(radius);
		bodyFixture = body.createFixture(shape,  k);
		bodyFixture.setFriction(0f);
		body.setType(BodyType.DYNAMIC);
		
		Filter filter = new Filter();
		filter.categoryBits = CollisionFlag.GROUP_1.getValue() ;
		filter.maskBits = CollisionFlag.GROUP_1.getValue() ;
		bodyFixture.setFilterData(filter);
		
		body.setFixedRotation(true);
		PolygonShape footShape = new PolygonShape();
		footShape.setAsBox(0.3f, 0.1f, new Vec2(0f,-0.4f), 0f);
		footFixture = body.createFixture(footShape, 0f);
		footFixture.setSensor(true);
		
		footFixture.setFilterData(filter);
		
		// Drawing Rect
		physRect = new PhysicsRect(1, 1);
	}
	
	private void applyConstants() {
		setMaxJumpVelocity(GameSettings.ARARA_MAX_JUMP_VELOCITY);
		setMaxRunVelocity(GameSettings.ARARA_MAX_RUN_VELOCITY);
		setJumpAceleration(GameSettings.ARARA_JUMP_ACELERATION);
		setRunAceleration(GameSettings.ARARA_RUN_ACELERATION);
		setTimeWaitingForAct(GameSettings.ARARA_TIME_WAITING_FOR_ACT);
		setMaxDoubleJumps(1);
	}
	
	public void jump() {
		
		if (hasFinished() || !isAlive() || contJump > 0 || (bodiesContact.size() <= 0 && doubleJump == 0))
			return;
		if(bodiesContact.size() <= 0 && doubleJump > 0){
			doubleJump--;
		} else {
			doubleJump = getMaxDoubleJumps();
		}
		sprite.setAnimationState("default"); // jump
//		Math.max(Math.min(,(MAX_JUMP_VELOCITY - body.getLinearVelocity().y)
		float impulseX = (getJumpAceleration()-body.getLinearVelocity().y) * body.getMass();
		Vec2 direction = new Vec2(0,6);
		direction.normalize();
		direction.mulLocal(impulseX);
		body.applyLinearImpulse(direction, body.getWorldCenter());
		contJump = 5;
		applyReturn(direction);
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
        if (shouldStop()) {
            stopAction();
            return;
        }
		if (jumpActive) {
			jump();
			jumpActive = false;
		}
		if(actActive){
			act();
			sprite.setAnimationState("default"); // act
		}
		if(contJump > 0) contJump--;
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
	
	@Override
	public int getStatusImg() {
		return R.drawable.arara_status;
	}
}
