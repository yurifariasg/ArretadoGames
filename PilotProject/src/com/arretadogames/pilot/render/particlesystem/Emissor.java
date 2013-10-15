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
	
	public Emissor(Vec2 location, Vec2 velocity, ParticleType pType){
		this.eLocation = location;
		this.velocity = velocity;
		setParticlesType(pType);
	}
	
	private void setParticlesType(ParticleType p){
		switch (p) {
		case FIRE_PARTICLE:
			particles = new FireParticle[NUMBER_OF_PARTICLES];
			for (int i = 0; i < NUMBER_OF_PARTICLES; i++) {
				particles[i] = new FireParticle( this.eLocation.clone(), (float)Math.random() * 2f );
			}
			break;
		case WATER_PARTICLE:
			particles = new WaterParticle[NUMBER_OF_PARTICLES];
			for (int i = 0; i < NUMBER_OF_PARTICLES; i++) {
				particles[i] = new WaterParticle( this.eLocation.clone(), (float)Math.random() * 2f );
			}
			break;
		case SAND_PARTICLE:
			particles = new SandParticle[NUMBER_OF_PARTICLES];
			for (int i = 0; i < NUMBER_OF_PARTICLES; i++) {
				particles[i] = new SandParticle( this.eLocation.clone(), (float)Math.random() * 2f );
			}
			break;
		default:
			break;
		}
	}

	@Override
	public void step(float timeElapsed) {
		eLocation.addLocal(velocity);
	}

	@Override
	public void render(GLCanvas canvas, float timeElapsed) {
//		Particle p;
//		for (int i = 0; i < particles.length; i++){
//			p = particles[i];
//			if (p.isDead()){
//				if (p.getType() == ParticleType.FIRE_PARTICLE)
//					p = new FireParticle(this.eLocation.clone(), (float)Math.random() * 2f);
//				
//				else if(p.getType() == ParticleType.WATER_PARTICLE)
//					p = new WaterParticle(this.eLocation.clone(), (float)Math.random() * 2f);
//				
//				else
//					p = new SandParticle(this.eLocation.clone(), (float)Math.random() * 2f);
//			}
//			p.step(timeElapsed);
//			p.render(canvas, timeElapsed);
//		}
		for (int i = 0; i < NUMBER_OF_PARTICLES; i++){
			if (particles[i].isDead()){
				if (particles[i].getType() == ParticleType.FIRE_PARTICLE)
					particles[i] = new FireParticle(this.eLocation.clone(), (float)Math.random() * 2f);
				
				else if (particles[i].getType() == ParticleType.WATER_PARTICLE)
					particles[i] = new WaterParticle(this.eLocation.clone(), (float)Math.random() * 2f);
				
				else if (particles[i].getType() == ParticleType.SAND_PARTICLE)
					particles[i] = new SandParticle(this.eLocation.clone(), (float)Math.random() * 2f);
			}
			
			particles[i].step(timeElapsed);
			particles[i].render(canvas, timeElapsed);
		}
		
		
	}
}
