package com.arretadogames.pilot.entities;

import java.util.Date;

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.BodyType;

import com.arretadogames.pilot.R;
import com.arretadogames.pilot.config.GameSettings;
import com.arretadogames.pilot.render.PhysicsRect;
import com.arretadogames.pilot.render.opengl.GLCanvas;

public class LoboGuara extends Player {
	
	private static final Vec2 BODY_DIMEN = new Vec2(0.4f, 0.8f);

	Date lastAct;
	private float timeForNextAct = 0f;
	private int doubleJump;
	
	public LoboGuara(float x, float y, PlayerNumber number) {
		super(x, y, number);
		applyConstants();
		doubleJump = getMaxDoubleJumps();
		
		float radius = BODY_DIMEN.x / 2f;
		
		CircleShape shape = new CircleShape();
		shape.setRadius(radius);
		shape.m_p.set(0f, (- BODY_DIMEN.y / 2f) + radius);
		footFixture = body.createFixture(shape, 0.1f);
		footFixture.setFriction(0f);
		// FOOT OK
		
		// Head
		shape = new CircleShape();
		shape.setRadius(radius);
		shape.m_p.set(0f, (BODY_DIMEN.y / 2f) - radius);
		body.createFixture(shape, 0.1f); // HEAD
		

		PolygonShape bodyShape = new PolygonShape();
		bodyShape.setAsBox(BODY_DIMEN.x / 2f, BODY_DIMEN.y / 2f - radius);
		bodyFixture = body.createFixture(bodyShape,  2.8f);
		bodyFixture.setFriction(0f);
		
		body.setType(BodyType.DYNAMIC);
		contJump = 0;
		body.setFixedRotation(true);

        categoryBits = CollisionFlag.GROUP_PLAYERS.getValue() ;
        maskBits = CollisionFlag.GROUP_COMMON_ENTITIES.getValue() | CollisionFlag.GROUP_GROUND.getValue()
                | CollisionFlag.GROUP_PLAYERS.getValue();
        
        setMaskAndCategoryBits();
		
		physRect = new PhysicsRect(1.8f, 1.8f);
	}
	
	public void applyConstants() {
		setMaxJumpVelocity(GameSettings.LOBO_MAX_JUMP_VELOCITY);
		setMaxRunVelocity(GameSettings.LOBO_MAX_RUN_VELOCITY);
		setJumpAceleration(GameSettings.LOBO_JUMP_ACELERATION);
		setRunAceleration(GameSettings.LOBO_RUN_ACELERATION);
		setTimeWaitingForAct(GameSettings.LOBO_TIME_WAITING_FOR_ACT);
		setMaxDoubleJumps(0);
	}

	public void jump() {
		if (hasFinished() || !isAlive() || contJump > 0 || (bodiesContact.size() <= 0 && doubleJump == 0))
			return;
		if(bodiesContact.size() <= 0 && doubleJump > 0){
			doubleJump--;
		} else {
			doubleJump = getMaxDoubleJumps() ;
		}
		
		sprite.setAnimationState("jump");
		float impulseX = (getJumpAceleration()-body.getLinearVelocity().y) * body.getMass();
		Vec2 direction = new Vec2(0,6); // TODO: Add this as a constant
		direction.normalize();
		direction.mulLocal(impulseX);
		body.applyLinearImpulse(direction, body.getWorldCenter(), true);
		contJump = 5;
		applyReturn(direction);
	}
	
	public void run(){
		if(body.getLinearVelocity().x < 0.5){ 
			body.applyLinearImpulse(new Vec2(0.5f * body.getMass(),0f), body.getWorldCenter(), true);
		}
		if(body.getLinearVelocity().length() > 8){
			Vec2 vel = body.getLinearVelocity().clone();
			vel.normalize();
			body.setLinearVelocity(vel.mul(8));
		}
		if(bodiesContact.size() > 0 && body.getLinearVelocity().x < getMaxRunVelocity()){
			float force = (getRunAceleration()) * body.getMass();
			Vec2 direction = new Vec2(1,0);
			direction.normalize();
			direction.mulLocal(force);
			body.applyForceToCenter(direction);
		}
	}

	public void act() {	
		if( bodiesContact.size() > 0 && contAct == 0){
			if( getTimeForNextAct() < 0.00000001 ){
			setTimeForNextAct(getTimeWaitingForAct());
			float impulse = (3) * body.getMass();
			Vec2 direction = new Vec2(1,0);
			direction.normalize();
			direction.mulLocal(impulse);
			body.applyLinearImpulse(direction, body.getWorldCenter(), true);
			contAct = 50;
			lastAct = new Date();
		}
		}
	}
	
	@Override
	public int getPercentageLeftToNextAct() {
		return Math.min((int)((((getTimeWaitingForAct()-getTimeForNextAct())/getTimeWaitingForAct()) * 100) + 0.000000001),100);
	}
	
	
	
	@Override
	public void playerStep(float timeElapsed) {
		setTimeForNextAct(Math.max(0.0f,getTimeForNextAct()-timeElapsed));
	}
	
	@Override
	public void playerRender(GLCanvas canvas, float timeElapsed) {
		canvas.saveState();
		
        if (body.getLinearVelocity().x > 3.5f) {
            sprite.setAnimationState("run");
        }
		
		canvas.translatePhysics(getPosX(), getPosY() + 0.39f);
		canvas.rotate((float) (180 * - getAngle() / Math.PI));
        sprite.render(canvas, physRect, timeElapsed);
        
		canvas.restoreState();
		
		
	}
	
	@Override
	public int getStatusImg() {
		return R.drawable.lobo_status;
	}

	public float getTimeForNextAct() {
		return timeForNextAct;
	}

	public void setTimeForNextAct(float timeForNextAct) {
		this.timeForNextAct = timeForNextAct;
	}
}
