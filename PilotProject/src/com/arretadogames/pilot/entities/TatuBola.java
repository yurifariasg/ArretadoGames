package com.arretadogames.pilot.entities;

import com.arretadogames.pilot.R;
import com.arretadogames.pilot.config.GameSettings;
import com.arretadogames.pilot.entities.effects.EffectDescriptor;
import com.arretadogames.pilot.entities.effects.EffectManager;
import com.arretadogames.pilot.entities.effects.PostEffectCallback;
import com.arretadogames.pilot.render.PhysicsRect;
import com.arretadogames.pilot.render.opengl.GLCanvas;

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyType;

import java.util.HashSet;

public class TatuBola extends Player implements PostEffectCallback {

	private final float rad = 0.2f;
	private int doubleJump;
	
	public TatuBola(float x, float y, PlayerNumber number) {
		super(x, y, number,
		        GameSettings.TATU_TIME_WAITING_FOR_ACT,
		        GameSettings.TATU_DASH_DURATION);
		applyConstants();
		doubleJump = getMaxDoubleJumps();

		CircleShape shape = new CircleShape();
		shape.setRadius(rad);
		bodyFixture = body.createFixture(shape,  6f);
		bodyFixture.setFriction(0f);
		body.setType(BodyType.DYNAMIC);
		contJump = 0;
		body.setFixedRotation(true);
		PolygonShape footShape = new PolygonShape();
		footShape.setAsBox(rad, 0.1f, new Vec2(0f,-rad + 0.1f), 0f);
		footFixture = body.createFixture(footShape, 0f);
		footFixture.setSensor(true);
		bodiesContact = new HashSet<Body>();

        categoryBits = CollisionFlag.GROUP_PLAYERS.getValue() | CollisionFlag.GROUP_TATU_HOLE.getValue();
        maskBits = CollisionFlag.GROUP_COMMON_ENTITIES.getValue() | CollisionFlag.GROUP_TATU_HOLE.getValue() 
                | CollisionFlag.GROUP_GROUND.getValue() | CollisionFlag.GROUP_PLAYERS.getValue();
        
        setMaskAndCategoryBits();
		
		physRect = new PhysicsRect(rad + 0.5f, rad + 0.5f);
	}
	
	public void applyConstants() {
		setMaxJumpVelocity(GameSettings.TATU_MAX_JUMP_VELOCITY);
		setMaxRunVelocity(GameSettings.TATU_MAX_RUN_VELOCITY);
		setJumpAceleration(GameSettings.TATU_JUMP_ACELERATION);
		setRunAceleration(GameSettings.TATU_RUN_ACELERATION, GameSettings.ARARA_INITIAL_RUN_ACELERATION);
		setMaxDoubleJumps(0);
	}
	
	@Override
	public PolygonShape getWaterContactShape() {
		PolygonShape a = new PolygonShape();
		a.setAsBox(rad, rad);
		return a;
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
		Vec2 direction = new Vec2(2,6);
		direction.normalize();
		direction.mulLocal(impulseX);
		body.applyLinearImpulse(direction, body.getWorldCenter(), true);
		contJump = 5;
		applyReturn(direction);
		
	}
	
	public void run(){
	    if (!isDashActive()) {
    		if(body.getLinearVelocity().x < getMaxRunVelocity()){
    			float force = (getRunAceleration()) * body.getMass();
    			//Vec2 direction = new Vec2((float)Math.cos(body.getAngle() ),(float)Math.sin(body.getAngle()));
    			Vec2 direction = new Vec2(1,0);
    			direction.normalize();
    			direction.mulLocal(force);
    			body.applyForceToCenter(direction);
    		}
	    } else {
	        sprite.setAnimationState("running");
            body.getLinearVelocity().x =
                    getMaxRunVelocity() * GameSettings.DASH_MAX_VEL_MULTIPLIER;
        }
	}

	public boolean dash() {
	    shouldLimitVelocity = false;
        EffectDescriptor descriptor = new EffectDescriptor();
        descriptor.pRect = physRect.clone();
        descriptor.pRect.inset(-0.2f, -0.2f);
        descriptor.position = body.getPosition();
        descriptor.duration = GameSettings.TATU_DASH_DURATION;
        descriptor.type = "speed_burst";
        descriptor.xOffset -= 0.2f;
        descriptor.alpha = 100;
        descriptor.callback = this;
        
        EffectManager.getInstance().addEffect(descriptor);
	    return true;
	}

	@Override
	public void playerStep(float timeElapsed) {
		if( bodiesContact.size() > 0 && !actActive){
			sprite.setAnimationState("default");
		}
	}
	
	@Override
	public void playerRender(GLCanvas canvas, float timeElapsed) {
		canvas.saveState();
		canvas.translatePhysics(getPosX(), getPosY() + 0.07f);
		canvas.rotate((float) (180 * - getAngle() / Math.PI));
        sprite.render(canvas, physRect, timeElapsed);
		canvas.restoreState();
		
	}
	
	@Override
	public int getStatusImg() {
		return R.drawable.tatu_status;
	}

    @Override
    public void finished() {
        body.getLinearVelocity().x *= GameSettings.AFTER_DASH_DAMPING;
        shouldLimitVelocity = true;
    }
}
