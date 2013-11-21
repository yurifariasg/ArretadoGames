package com.arretadogames.pilot.entities;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.contacts.Contact;

import com.arretadogames.pilot.R;
import com.arretadogames.pilot.physics.PhysicalWorld;
import com.arretadogames.pilot.render.Sprite;
import com.arretadogames.pilot.render.opengl.GLCanvas;
import com.arretadogames.pilot.render.particlesystem.FireParticle;

public class Fire extends Entity implements Steppable {

	private float base_velocity = 2.6f; // 3.2f
	private float velocity = 3.2f;//3.2f;
	
	private static final float WIDTH = 10;
	private static final float HEIGHT = 20;
	private static final float SMALL_FIXTURE_WIDTH = 0.5f;
	
	// Rendering
	private static final int MAX_PARTICLES = 200;
	private static final float PARTICLES_PER_SECOND = 30;
	
	private float currentParticlesToCreate;
	private FireParticle[] particles = new FireParticle[MAX_PARTICLES];
	
	private Fixture fireFixture;
	private Vec2 velVector = new Vec2(velocity ,0);
	
	public Fire(float x, float y) {
		super(x, 0); // Fire will always be on Y = 0
		
		// This fixture will be only used to create an area where the Fire will appears on the camera
		// (It will collide and be shown on screen)
		PolygonShape shape = new PolygonShape();
		shape.setAsBox(WIDTH/2, HEIGHT/2);
		Fixture f = body.createFixture(shape,  1f);
		f.setSensor(true);
		
		// Create the destroyer-fixture
		shape = new PolygonShape();
		shape.setAsBox(SMALL_FIXTURE_WIDTH / 2, HEIGHT / 2, new Vec2(0, 0), 0); // 45
		fireFixture = body.createFixture(shape, 1f);
		fireFixture.setSensor(true);
		
		body.setType(BodyType.KINEMATIC);
		// Create Particles
		for (int i = 0 ; i < MAX_PARTICLES ; i++) {
			particles[i] = new FireParticle(new Vec2(), 0);
		}
	}
	
	private FireParticle recycleParticle(FireParticle aux) {
		
		aux.setLifespan((float) (1.5 + 0.5 * Math.random()));
		aux.getLocation().x = getPosX() + SMALL_FIXTURE_WIDTH;
		aux.getLocation().y = 0;
		
		return aux;
	}

	@Override
	public void render(GLCanvas canvas, float timeElapsed) {
		FireParticle aux;
		for (int i = 0 ; i < MAX_PARTICLES ; i++) {
			aux = particles[i];
			if (!aux.isDead()) {
				aux.render(canvas, timeElapsed);
			}
		}
	}

	@Override
	public int getLayerPosition() {
		return -100;
	}
	
	@Override
	public void beginContact(Entity e, Contact contact) {
		super.endContact(e, contact);
		if (contact.getFixtureA().equals(fireFixture) ||
				contact.getFixtureB().equals(fireFixture))
			PhysicalWorld.getInstance().addDeadEntity(e);
	}

	@Override
	public void step(float timeElapsed) {
		velVector.x = velocity;
		body.setLinearVelocity(velVector);
	
		currentParticlesToCreate += (PARTICLES_PER_SECOND * timeElapsed);
		FireParticle aux;
		for (int i = 0 ; i < MAX_PARTICLES ; i++) {
			aux = particles[i];
			if (aux.isDead() && currentParticlesToCreate > 0) {
				// Recycle
				particles[i] = recycleParticle(aux);
				currentParticlesToCreate--;
			}
			
			if (!aux.isDead())
				aux.step(timeElapsed);
		}
	}

	@Override
	public EntityType getType() {
		return EntityType.FIRE;
	}

	@Override
	public void setSprite(Sprite sprite) {
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
