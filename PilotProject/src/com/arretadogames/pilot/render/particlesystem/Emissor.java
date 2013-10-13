package com.arretadogames.pilot.render.particlesystem;

import org.jbox2d.common.Vec2;
import com.arretadogames.pilot.entities.Steppable;
import com.arretadogames.pilot.render.Renderable;
import com.arretadogames.pilot.render.opengl.GLCanvas;

public class Emissor implements Renderable, Steppable{
	
	private final int NUMBER_OF_PARTICLES = 50;
	private final Vec2 eLocation;
	private Vec2 velocity;
	private Particle[] particles;
	
	public Emissor(Vec2 location, Vec2 velocity){
		this.eLocation = location;
		this.velocity = velocity;
		particles = new Particle[NUMBER_OF_PARTICLES];
		setUpParticles();
	}

	private void setUpParticles() {
		for (int i = 0; i < NUMBER_OF_PARTICLES; i++) {
			particles[i] = new Particle( this.eLocation.clone(), new Vec2(0.025f, 0.025f), 1f, 0.4f, 3f );
		}
	}

	@Override
	public void step(float timeElapsed) {
//		location.addLocal(velocity);
	}

	@Override
	public void render(GLCanvas canvas, float timeElapsed) {
		for (int i = 0; i < NUMBER_OF_PARTICLES; i++) {
			if (particles[i].isDead()){
				particles[i].setLocation(this.eLocation);
			}
			particles[i].step(timeElapsed);
			particles[i].render(canvas, timeElapsed);
		}
	}
}

/*
p = new Particle(	
location		new Vec2(GameSettings.TARGET_WIDTH/2, GameSettings.TARGET_HEIGHT/2),
aceleration		new Vec2(GameSettings.TARGET_WIDTH/2,GameSettings.TARGET_HEIGHT/2),
velx			1f,
vely		 	0.4f,
lifespan		3f);
*/
