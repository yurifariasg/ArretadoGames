package com.arretadogames.pilot.entities;

import com.arretadogames.pilot.R;
import com.arretadogames.pilot.config.GameSettings;
import com.arretadogames.pilot.entities.effects.EffectDescriptor;
import com.arretadogames.pilot.entities.effects.EffectManager;
import com.arretadogames.pilot.entities.effects.PostEffectCallback;
import com.arretadogames.pilot.render.PhysicsRect;
import com.arretadogames.pilot.render.opengl.GLCanvas;
import com.arretadogames.pilot.util.Assets;

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.BodyType;

public class AraraAzul extends Player implements Steppable, PostEffectCallback {
    
    private static final float DOUBLE_JUMP_IMG_DURATION = 0.1f;

    private final float ARARA_DENSITY = 3f;
	private int doubleJump;
	private float radius;
	
	
	private float timerToDoubleJumpAnimation;
	
	public AraraAzul(float x, float y, PlayerNumber number) {
		super(x, y, number,
		        GameSettings.ARARA_TIME_WAITING_FOR_ACT,
		        GameSettings.ARARA_DASH_DURATION);
		applyConstants();
		doubleJump = getMaxDoubleJumps();

		CircleShape shape = new CircleShape();
		radius = 0.2f;
		shape.setRadius(radius);
		bodyFixture = body.createFixture(shape,  ARARA_DENSITY);
		bodyFixture.setFriction(0f);
		body.setType(BodyType.DYNAMIC);
		
		body.setFixedRotation(true);
		PolygonShape footShape = new PolygonShape();
		footShape.setAsBox(radius, 0.1f, new Vec2(0f, - radius), 0f);
		footFixture = body.createFixture(footShape, 0f);
		footFixture.setSensor(true);
		
		body.setGravityScale(0.6f);
		
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
		setRunAceleration(GameSettings.ARARA_RUN_ACELERATION, GameSettings.ARARA_INITIAL_RUN_ACELERATION);
		setMaxDoubleJumps(1);
	}
	
	public void jump() {
		if (hasFinished() || !isAlive() || contJump > 0 || (bodiesContact.size() <= 0 && doubleJump == 0))
			return;
		if(bodiesContact.size() <= 0 && doubleJump > 0){
			doubleJump--;
			timerToDoubleJumpAnimation = DOUBLE_JUMP_IMG_DURATION; // Double Jump!
		} else {
			doubleJump = getMaxDoubleJumps();
		}
		sprite.setAnimationState("default"); // jump
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
    			Vec2 direction = new Vec2(1,0);
    			direction.normalize();
    			direction.mulLocal(force);
    			body.applyForceToCenter(direction);
    		}
	    } else {
            body.getLinearVelocity().x =
                    getMaxRunVelocity() * GameSettings.DASH_MAX_VEL_MULTIPLIER;
            body.getLinearVelocity().y = 0;
	    }
	}
	
	public boolean dash() {
	    if (bodiesContact.size() <= 0) {
            shouldLimitVelocity = false;
            EffectDescriptor descriptor = new EffectDescriptor();
            descriptor.pRect = physRect.clone();
            descriptor.pRect.inset(-0.2f, -0.2f);
            descriptor.position = body.getPosition();
            descriptor.duration = GameSettings.ARARA_DASH_DURATION;
            descriptor.type = "speed_burst";
            descriptor.xOffset -= 0.2f;
            descriptor.alpha = 100;
            descriptor.callback = this;
            EffectManager.getInstance().addEffect(descriptor);
            return true;
	    }
	    return false;
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
		
        if (timerToDoubleJumpAnimation > 0) {
            canvas.saveState();
            canvas.translatePhysics(getPosX(), getPosY() - 0.3f);
            canvas.drawBitmap(R.drawable.jump, physRect);
            timerToDoubleJumpAnimation -= timeElapsed;
            canvas.restoreState();
        }
		
	}
	
	@Override
	public int getStatusImg() {
		return R.drawable.arara_status;
	}

    @Override
    public void finished() {
        body.getLinearVelocity().x *= GameSettings.AFTER_DASH_DAMPING;
        shouldLimitVelocity = true;
    }
}
