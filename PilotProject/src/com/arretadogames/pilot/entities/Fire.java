package com.arretadogames.pilot.entities;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.Filter;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.contacts.Contact;

import com.arretadogames.pilot.R;
import com.arretadogames.pilot.entities.effects.EffectDescriptor;
import com.arretadogames.pilot.entities.effects.EffectManager;
import com.arretadogames.pilot.render.AnimationSwitcher;
import com.arretadogames.pilot.render.opengl.GLCanvas;
import com.arretadogames.pilot.render.particlesystem.Flame;

public class Fire extends Entity implements Steppable {

	private float base_velocity = 2.6f; // 3.2f
	private float velocity = 3.2f;//3.2f;
	
	private static final float WIDTH = 10;
	private static final float HEIGHT = 20;
	private static final float SMALL_FIXTURE_WIDTH = 0.5f;
	
	private Fixture fireFixture;
	private Vec2 velVector = new Vec2(velocity, 0);
	
	private static final int FLAMES_QUANTITY = 4;
	
	private Flame[] flames;
	private static final float DISTANCE_TO_SPAWN_FLAME = 0.5f;
	
	private EffectDescriptor killEntityEffect;
	
	public Fire(float x, float y) {
		super(x, 0); // Fire will always be on Y = 0
		
		flames = new Flame[FLAMES_QUANTITY];
		for (int i = FLAMES_QUANTITY - 1 ; i >= 0 ; i--) {
		    flames[i] = new Flame(body.getPosition(), i * DISTANCE_TO_SPAWN_FLAME, (FLAMES_QUANTITY - i + 1) * 0.7f);
		}
		
		// This fixture will be only used to create an area where the Fire will appears on the camera
		// (It will collide and be shown on screen)
		PolygonShape shape = new PolygonShape();
		shape.setAsBox(WIDTH/2, HEIGHT/2);
		Fixture f = body.createFixture(shape,  1f);
		f.setSensor(true);
		Filter filter = new Filter();
		filter.categoryBits = CollisionFlag.GROUP_3.getValue() ;
		filter.maskBits = f.m_filter.maskBits ;
		f.setFilterData(filter);
		
		// Create the destroyer-fixture
		shape = new PolygonShape();
		shape.setAsBox(SMALL_FIXTURE_WIDTH / 2, HEIGHT / 2, new Vec2(-0.5f, 0), 60); // Rotates it 60 degrees and pushes it back a bit
		fireFixture = body.createFixture(shape, 1f);
		fireFixture.setSensor(true);
		
		body.setType(BodyType.KINEMATIC);
		
		// Effect to kill entities
		killEntityEffect = new EffectDescriptor();
		killEntityEffect.repeat = true;
		killEntityEffect.type = "Fire";
		killEntityEffect.layerPosition = getLayerPosition() + 1;
		killEntityEffect.duration = 10;
	}

	@Override
	public void render(GLCanvas canvas, float timeElapsed) {
	    for (int i = 0 ; i < flames.length ; i++) {
	        flames[i].render(canvas, timeElapsed);
	    }
	}

	@Override
	public int getLayerPosition() {
		return -50;
	}
	
	@Override
	public void beginContact(Entity e, Contact contact) {
		super.beginContact(e, contact);
		if (contact.getFixtureA().equals(fireFixture) ||
				contact.getFixtureB().equals(fireFixture)) {
			if (e.isAlive()) {
				e.kill();
				EffectDescriptor killEffect = killEntityEffect.clone();
				killEffect.position = e.body.getPosition();
				killEffect.pRect = e.physRect.clone();
				killEffect.pRect.inset(-0.5f, -0.5f);
				EffectManager.getInstance().addEffect(killEffect);
				
			}
//			PhysicalWorld.getInstance().addDeadEntity(e);
		}
	}

	@Override
	public void step(float timeElapsed) {
        velVector.x = velocity;
        body.setLinearVelocity(velVector);
        for (int i = 0 ; i < flames.length ; i++) {
            flames[i].step(timeElapsed);
        }
	}

	@Override
	public EntityType getType() {
		return EntityType.FIRE;
	}

	@Override
	public void setSprite(AnimationSwitcher sprite) {
		// TODO Auto-generated method stub
		
	}
	
	public int getStatusImg(){
		return R.drawable.fire_status;
	}

	public float getBaseVelocity(){
		return base_velocity;
	}

	public void setCurrentVelocity(float newVelocity) {
		velocity = newVelocity;
	}
	
}
