package com.arretadogames.pilot.entities;

import com.arretadogames.pilot.R;
import com.arretadogames.pilot.config.GameSettings;
import com.arretadogames.pilot.render.PhysicsRect;
import com.arretadogames.pilot.render.opengl.GLCanvas;

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.BodyType;

public class AraraAzul extends Player implements Steppable {

	private float k = 3f;
	private int doubleJump;
	private float radius;
	
	public AraraAzul(float x, float y, PlayerNumber number) {
		super(x, y, number);
		applyConstants();
		doubleJump = getMaxDoubleJumps();

		CircleShape shape = new CircleShape();
		radius = 0.2f;
		shape.setRadius(radius);
		bodyFixture = body.createFixture(shape,  k);
		bodyFixture.setFriction(0f);
		body.setType(BodyType.DYNAMIC);
		
		body.setFixedRotation(true);
		PolygonShape footShape = new PolygonShape();
		footShape.setAsBox(radius, 0.1f, new Vec2(0f, - radius), 0f);
		footFixture = body.createFixture(footShape, 0f);
		footFixture.setSensor(true);
		
        categoryBits = CollisionFlag.GROUP_PLAYERS.getValue() ;
        maskBits = CollisionFlag.GROUP_COMMON_ENTITIES.getValue() | CollisionFlag.GROUP_GROUND.getValue()
                | CollisionFlag.GROUP_PLAYERS.getValue();
        
        setMaskAndCategoryBits();
		
		// Drawing Rect
		physRect = new PhysicsRect(1, 1);
	}
	
	public void applyConstants() {
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
		float impulseX = (getJumpAceleration()-body.getLinearVelocity().y) * body.getMass();
		Vec2 direction = new Vec2(0,6);
		direction.normalize();
		direction.mulLocal(impulseX);
		body.applyLinearImpulse(direction, body.getWorldCenter(), true);
		contJump = 5;
		applyReturn(direction);
	}
	
	public void run(){
		if(body.getLinearVelocity().x < 0.5){ 
			body.applyLinearImpulse(new Vec2(0.65f * body.getMass(),0f), body.getWorldCenter(), true);
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
		if (hasFinished() || !isAlive())
			return;
		float vel = body.getLinearVelocity().y;
		float velx = body.getLinearVelocity().x;
		Vec2 force = new Vec2((k * vel * vel) / 3, k * vel * vel);
		if( vel < 0 ){
			body.applyForce(force, body.getWorldCenter());
		}
		Vec2 forcex = new Vec2(-(velx * velx)/20 ,0);
		if( velx > 0 ){
			body.applyForce(forcex, body.getWorldCenter());
		}
	}

	@Override
	public void playerStep(float timeElapsed) {
	}
	
	@Override
	public void playerRender(GLCanvas canvas, float timeElapsed) {
		
		canvas.saveState();
		canvas.translatePhysics(getPosX(), getPosY() + 0.1f);
		canvas.rotate((float) (180 * - getAngle() / Math.PI));
		sprite.render(canvas, physRect, timeElapsed);
		canvas.restoreState();
		
	}
	
	@Override
	public int getStatusImg() {
		return R.drawable.arara_status;
	}
}
