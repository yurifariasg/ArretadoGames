package com.arretadogames.pilot.entities;

import com.arretadogames.pilot.R;
import com.arretadogames.pilot.config.GameSettings;
import com.arretadogames.pilot.entities.effects.EffectDescriptor;
import com.arretadogames.pilot.entities.effects.EffectManager;
import com.arretadogames.pilot.entities.effects.PostEffectCallback;
import com.arretadogames.pilot.render.PhysicsRect;
import com.arretadogames.pilot.render.opengl.GLCanvas;
import com.arretadogames.pilot.util.Util;

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyType;

public class MacacoPrego extends Player implements Steppable, PostEffectCallback{

    private static final Vec2 SUPER_JUMP_IMPULSE = new Vec2(GameSettings.DASH_MAX_VEL_MULTIPLIER,
            GameSettings.DASH_MAX_VEL_MULTIPLIER);
    private static final PhysicsRect BODY_DIMEN = new PhysicsRect(0.4f, 0.75f);
	private Body b;
	private int doubleJump;
	private boolean shouldSuperJump;
	
	
	public MacacoPrego(float x, float y, PlayerNumber number) {
		super(x, y, number,
		        GameSettings.MACACO_TIME_WAITING_FOR_ACT,
		        GameSettings.MACACO_DASH_DURATION);
		applyConstants();
		doubleJump = getMaxDoubleJumps();
		shouldSuperJump = false;
		
		float radius = BODY_DIMEN.width() / 2f;
        
        CircleShape shape = new CircleShape();
        shape.setRadius(radius);
        shape.m_p.set(0f, (- BODY_DIMEN.height() / 2f) + radius);
        footFixture = body.createFixture(shape, 0.1f);
        footFixture.setFriction(0f);
        
        // Head
        shape = new CircleShape();
        shape.setRadius(radius);
        shape.m_p.set(0f, (BODY_DIMEN.height() / 2f) - radius);
        body.createFixture(shape, 0.1f); // HEAD

        PolygonShape bodyShape = new PolygonShape();
        bodyShape.setAsBox(BODY_DIMEN.width() / 2f, BODY_DIMEN.height() / 2f - radius);
        bodyFixture = body.createFixture(bodyShape,  2.8f); // 2.8f
        bodyFixture.setFriction(0f);
        
        body.setType(BodyType.DYNAMIC);
        contJump = 0;
        body.setFixedRotation(true);

        categoryBits = CollisionFlag.GROUP_PLAYERS.getValue() ;
        maskBits = CollisionFlag.GROUP_COMMON_ENTITIES.getValue() | CollisionFlag.GROUP_GROUND.getValue()
                | CollisionFlag.GROUP_PLAYERS.getValue();
        
        setMaskAndCategoryBits();
		
        // Image Rect
		physRect = Util.convertToSquare(BODY_DIMEN);
		physRect.inset(-0.1f, -0.1f);
	}
	
	public void applyConstants() {
		setMaxJumpVelocity(GameSettings.MACACO_MAX_JUMP_VELOCITY);
		setMaxRunVelocity(GameSettings.MACACO_MAX_RUN_VELOCITY);
		setJumpAceleration(GameSettings.MACACO_JUMP_ACELERATION);
		setRunAceleration(GameSettings.MACACO_RUN_ACELERATION, GameSettings.MACACO_INITIAL_RUN_ACELERATION);
		setMaxDoubleJumps(0);
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
		Vec2 direction = new Vec2(2,6);
		direction.normalize();
		direction.mulLocal(impulseX);
		body.applyLinearImpulse(direction, body.getWorldCenter(), true);
		contJump = 5;
		applyReturn(direction);
	}
	
	public void run(){
		if(body.getLinearVelocity().x < getMaxRunVelocity()){
			float force = (getRunAceleration()) * body.getMass();
			Vec2 direction = new Vec2(1,0);
			direction.normalize();
			direction.mulLocal(force);
			body.applyForceToCenter(direction);
		}
	}

	@Override
	public void playerStep(float timeElapsed) {
	    if (shouldSuperJump) {
	        shouldSuperJump = false;
	        body.applyLinearImpulse(SUPER_JUMP_IMPULSE.mul(body.getMass() * getJumpAceleration()), body.getPosition(), true);
	    }
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

    @Override
    public boolean dash() {
        if (bodiesContact.size() > 0) {
            shouldSuperJump = true;
            
            EffectDescriptor descriptor = new EffectDescriptor();
            descriptor.pRect = physRect.clone();
            descriptor.pRect.inset(-0.2f, -0.2f);
            descriptor.position = body.getPosition();
            descriptor.duration = GameSettings.MACACO_DASH_DURATION;
            descriptor.type = "speed_burst";
            descriptor.xOffset -= 0.2f;
            descriptor.yOffset -= 0.1f;
            descriptor.alpha = 100;
            descriptor.callback = this;
            descriptor.angle = -45;
            
            EffectManager.getInstance().addEffect(descriptor);
            return true;
        }
        return false;
    }

    @Override
    public void finished() {
    }
}
