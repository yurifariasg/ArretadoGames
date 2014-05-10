package com.arretadogames.pilot.entities;



import com.arretadogames.pilot.R;
import com.arretadogames.pilot.config.GameSettings;
import com.arretadogames.pilot.render.PhysicsRect;
import com.arretadogames.pilot.render.opengl.GLCanvas;
import com.arretadogames.pilot.util.Assets;

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.Filter;

import java.util.Date;
import java.util.HashSet;

public class TatuBola extends Player {

	Date lastAct;
	private final float TIME_WAITING_FOR_ACT = 6f;
	private float timeForNextAct = 0f;
	
	private final float rad = 0.3f;
	private int doubleJump;
	
	public TatuBola(float x, float y, PlayerNumber number) {
		super(x, y, number);
		applyConstants();
		doubleJump = getMaxDoubleJumps();

		CircleShape shape = new CircleShape();
		shape.setRadius(rad);
		bodyFixture = body.createFixture(shape,  3f);
		bodyFixture.setFriction(0f);
		Filter filter = new Filter();
		filter.categoryBits = CollisionFlag.GROUP_1.getValue() | CollisionFlag.GROUP_2.getValue();
		filter.maskBits = CollisionFlag.GROUP_1.getValue() | CollisionFlag.GROUP_2.getValue();
		bodyFixture.setFilterData(filter);
		body.setType(BodyType.DYNAMIC);
		contJump = 0;
		body.setFixedRotation(true);
		PolygonShape footShape = new PolygonShape();
		footShape.setAsBox(rad, 0.1f, new Vec2(0f,-rad + 0.1f), 0f);
		footFixture = body.createFixture(footShape, 0f);
		footFixture.setSensor(true);
		bodiesContact = new HashSet<Body>();
		
		physRect = new PhysicsRect(rad + 0.5f, rad + 0.5f);
	}
	private void applyConstants() {
		setMaxJumpVelocity(GameSettings.TATU_MAX_JUMP_VELOCITY);
		setMaxRunVelocity(GameSettings.TATU_MAX_RUN_VELOCITY);
		setJumpAceleration(GameSettings.TATU_JUMP_ACELERATION);
		setRunAceleration(GameSettings.TATU_RUN_ACELERATION);
		setTimeWaitingForAct(GameSettings.TATU_TIME_WAITING_FOR_ACT);
		setMaxDoubleJumps(0);
	}
	
	@Override
	public PolygonShape getWaterContactShape() {
		PolygonShape a = new PolygonShape();
		a.setAsBox(rad, rad);
		return a;
	}
	
	@Override
	public int getPercentageLeftToNextAct() {
		return Math.min((int)((((TIME_WAITING_FOR_ACT-timeForNextAct)/TIME_WAITING_FOR_ACT) * 100) + 0.000000001),100);
	}

	public void jump() {
		if (isGhostMode() || hasFinished() || !isAlive() || contJump > 0 || (bodiesContact.size() <= 0 && doubleJump == 0))
			return;
		if(bodiesContact.size() <= 0 && doubleJump > 0){
			doubleJump--;
		} else {
			doubleJump = getMaxDoubleJumps();
		}
		
		sprite.setAnimationState("jump");
		float impulseX = (getJumpAceleration()-body.getLinearVelocity().y) * body.getMass();
		Vec2 direction = new Vec2(0,6);
		direction.normalize();
		direction.mulLocal(impulseX);
		body.applyLinearImpulse(direction, body.getWorldCenter(), true);
		contJump = 5;
		applyReturn(direction);
		
	}
	
	public void run(){
		if(body.getLinearVelocity().x < 1.5){ 
			body.applyLinearImpulse(new Vec2(1 * body.getMass(),0f), body.getWorldCenter(), true);
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
			if ( timeForNextAct < 0.00000001 ){
    			timeForNextAct = TIME_WAITING_FOR_ACT;	
    			sprite.setAnimationState("jump");
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
	public void step(float timeElapsed) {
		applyConstants();
		super.step(timeElapsed);
		timeForNextAct = Math.max(0.0f,timeForNextAct-timeElapsed);
        if (shouldStop() || !shouldAct()) {
            if (shouldStop()) {
                stopAction();
            }
		}
		if (jumpActive) {
			jump();
			Assets.playSound(Assets.jumpSound);
			jumpActive = false;
		}
		if(actActive){
			act();
		}
		
		Date t = new Date();
		if( bodiesContact.size() > 0 && !actActive && (lastAct == null || (t.getTime() - lastAct.getTime())/1000 > 3  )){
			sprite.setAnimationState("default");
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
	
	@Override
	public int getStatusImg() {
		return R.drawable.tatu_status;
	}
}
