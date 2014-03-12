package com.arretadogames.pilot.entities;

import com.arretadogames.pilot.physics.PhysicalWorld;
import com.arretadogames.pilot.render.PhysicsRect;
import com.arretadogames.pilot.render.AnimationSwitcher;
import com.arretadogames.pilot.render.opengl.GLCanvas;

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.contacts.Contact;

public class Coin extends Entity {
	
	private static final int DEFAULT_VALUE = 10;
	
//	public final static int[] FRAMES = {
//		R.drawable.seed0,R.drawable.seed1,R.drawable.seed2,R.drawable.seed1};
	
//	public final static float[] DURATION = {
//		0.08f, 0.08f, 0.08f, 0.08f
//	};
	
	private AnimationSwitcher sprite;
	private int value;
	
	public Coin(float x, float y, int value) {
		super(x, y);
		
		if (value == 0)
			this.value = DEFAULT_VALUE;
		else
			this.value = value;
		
		CircleShape shape = new CircleShape();
		shape.setRadius(0.5f);
		body.createFixture(shape, 0f).setSensor(true);
		body.setType(BodyType.KINEMATIC);
		
		physRect = new PhysicsRect(0.4f, 0.4f);
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
			Player p = (Player) e;
			p.addCoins(value);
			kill();
			PhysicalWorld.getInstance().addDeadEntity(this);
		}
	}

}