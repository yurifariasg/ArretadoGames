package com.arretadogames.pilot.entities;

import com.arretadogames.pilot.R;
import com.arretadogames.pilot.config.GameSettings;
import com.arretadogames.pilot.render.PhysicsRect;
import com.arretadogames.pilot.render.opengl.GLCanvas;

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.BodyType;

import java.util.Date;

public class LoboGuara extends Player {

	Date lastAct;
	private float size;
	private float timeForNextAct = 0f;
	private int doubleJump;
	
	public LoboGuara(float x, float y, PlayerNumber number) {
		super(x, y, number);
		applyConstants();
		doubleJump = getMaxDoubleJumps();
		
		CircleShape shape = new CircleShape();
		size = 0.5f;
		shape.setRadius(size);
		bodyFixture = body.createFixture(shape,  3f);
		bodyFixture.setFriction(0f);
		
		body.setType(BodyType.DYNAMIC);
		contJump = 0;
		body.setFixedRotation(true);
		PolygonShape footShape = new PolygonShape();
		footShape.setAsBox(0.35f, 0.1f, new Vec2(0f,-0.4f), 0f);
		footFixture = body.createFixture(footShape, 0f);
		footFixture.setSensor(true);

        categoryBits = CollisionFlag.GROUP_PLAYERS.getValue() ;
        maskBits = CollisionFlag.GROUP_COMMON_ENTITIES.getValue() | CollisionFlag.GROUP_GROUND.getValue()
                | CollisionFlag.GROUP_PLAYERS.getValue();
        
        setMaskAndCategoryBits();
		
		physRect = new PhysicsRect(1.6f, 1.8f); // 1.4, 1.6
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
		
		canvas.translatePhysics(getPosX(), getPosY() + 0.3f);
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
