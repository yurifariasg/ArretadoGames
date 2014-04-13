package com.arretadogames.pilot.entities;

import com.arretadogames.pilot.entities.effects.EffectDescriptor;
import com.arretadogames.pilot.entities.effects.EffectManager;
import com.arretadogames.pilot.physics.PhysicalWorld;
import com.arretadogames.pilot.render.AnimationSwitcher;
import com.arretadogames.pilot.render.PhysicsRect;
import com.arretadogames.pilot.render.opengl.GLCanvas;

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.contacts.Contact;

public class Coin extends Entity {
	
	private final static PhysicsRect SEED_RECT = new PhysicsRect(0.4f, 0.4f);
    private final static PhysicsRect SEED_EFFECT_RECT = new PhysicsRect(0.8f, 0.8f);
	private static EffectDescriptor dieAnimation;
	private AnimationSwitcher sprite;
	
	private static EffectDescriptor getDieEffect() {
	    if (dieAnimation == null) {
	        dieAnimation = new EffectDescriptor();
	        dieAnimation.type = "SeedFade";
	        dieAnimation.pRect = SEED_EFFECT_RECT;
	        dieAnimation.repeat = false;
	    }
	    return dieAnimation;
	}
	
	public Coin(float x, float y, int value) {
		super(x, y);
		
		CircleShape shape = new CircleShape();
		shape.setRadius(0.5f);
		body.createFixture(shape, 0f).setSensor(true);
		body.setType(BodyType.KINEMATIC);
		physRect = SEED_RECT;
		
	}

	@Override
	public void render(GLCanvas canvas, float timeElapsed) {
		canvas.saveState();
		canvas.translatePhysics(getPosX(), getPosY());
		canvas.rotate((float) (180 * - body.getAngle() / Math.PI));
        sprite.render(canvas, physRect, timeElapsed);
		canvas.restoreState();
		
	}

	@Override
	public EntityType getType() {
		return EntityType.SEED;
	}

	@Override
	public void setSprite(AnimationSwitcher sprite) {
		this.sprite = sprite;
	}
	
	@Override
	public void beginContact(Entity e, Contact contact) {
		super.beginContact(e, contact);
		if (e.getType() == EntityType.PLAYER && isAlive()) {
			kill();
			PhysicalWorld.getInstance().addDeadEntity(this);
		}
	}
	
	@Override
	public void kill() {
	    EffectDescriptor effect = getDieEffect();
	    effect.position = body.getPosition();
	    EffectManager.getInstance().addEffect(effect);
	    
	    super.kill();
	}

}