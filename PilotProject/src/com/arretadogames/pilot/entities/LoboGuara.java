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
import org.jbox2d.dynamics.BodyType;

public class LoboGuara extends Player implements PostEffectCallback {

	private static final Vec2 BODY_DIMEN = new Vec2(0.4f, 0.8f);
	private int doubleJump;
	
	public LoboGuara(float x, float y, PlayerNumber number) {
		super(x, y, number,
		        GameSettings.LOBO_TIME_WAITING_FOR_ACT,
		        GameSettings.LOBO_DASH_DURATION);
		applyConstants();
		doubleJump = getMaxDoubleJumps();
		
		float radius = BODY_DIMEN.x / 2f;
		
		CircleShape shape = new CircleShape();
		shape.setRadius(radius);
		shape.m_p.set(0f, (- BODY_DIMEN.y / 2f) + radius);
		footFixture = body.createFixture(shape, 0.1f);
		footFixture.setFriction(0f);

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
		setRunAceleration(GameSettings.LOBO_RUN_ACELERATION, GameSettings.ARARA_INITIAL_RUN_ACELERATION);
		setMaxDoubleJumps(0);
	}

	public void jump() {
		if (hasFinished() || isDashActive() || !isAlive() || contJump > 0 || (bodiesContact.size() <= 0 && doubleJump == 0))
			return;
		if(bodiesContact.size() <= 0 && doubleJump > 0){
			doubleJump--;
		} else {
			doubleJump = getMaxDoubleJumps() ;
		}
		
		sprite.setAnimationState("jump");
		float impulseX = (getJumpAceleration()-body.getLinearVelocity().y) * body.getMass();
		Vec2 direction = new Vec2(2,6); // TODO: Add this as constant
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
    			Vec2 direction = new Vec2(1,0);
    			direction.normalize();
    			direction.mulLocal(force);
    			body.applyForceToCenter(direction);
    		}
	    } else {
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
        descriptor.duration = GameSettings.LOBO_DASH_DURATION;
        descriptor.type = "speed_burst";
        descriptor.xOffset -= 0.65f;
        descriptor.alpha = 100;
        descriptor.callback = this;
        
        EffectManager.getInstance().addEffect(descriptor);
        return true;
	}
	
	@Override
	public void playerStep(float timeElapsed) {
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

    @Override
    public void finished() {
        body.getLinearVelocity().x *= GameSettings.AFTER_DASH_DAMPING;
        shouldLimitVelocity = true;
    }
}
