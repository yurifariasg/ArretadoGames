package com.arretadogames.pilot.render.particlesystem;

import com.arretadogames.pilot.entities.Steppable;
import com.arretadogames.pilot.render.Renderable;
import com.arretadogames.pilot.render.opengl.GLCanvas;

import org.jbox2d.common.Vec2;

/**
 * Flame is a container to fire particles
 */
public class Flame implements Renderable, Steppable {
    
    // Rendering
    private final int MAX_PARTICLES;// = 80; // 100
    private static final float PARTICLES_PER_SECOND = 30;
    
    private float currentParticlesToCreate;
    private FireParticle[] particles;
    
    private Vec2 location;
    
    private final float STARTING_LIFESPAN;
    private final float DISTANCE_OFFSET;
    
    public Flame(Vec2 location, float offsetDistance, float lifespan) { // Vec2 velocity) {
        this.location = location;
//        this.velocity = velocity;
        DISTANCE_OFFSET = offsetDistance;
        STARTING_LIFESPAN = lifespan;
        
        MAX_PARTICLES = (int) (lifespan * 40);
        particles = new FireParticle[MAX_PARTICLES];
        // Create Particles
        for (int i = 0 ; i < MAX_PARTICLES ; i++) { // TODO: Check Velocity here
            particles[i] = new FireParticle(new Vec2(), new Vec2( 0.00001f, (float) Math.random()*1f ), 0);
        }
    }
    
    @Override
    public void step(float timeElapsed) {
    
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
    public void render(GLCanvas canvas, float timeElapsed) {
        
        FireParticle aux;
        for (int i = 0 ; i < MAX_PARTICLES ; i++) {
            aux = particles[i];
            if (!aux.isDead()) {
                aux.render(canvas, timeElapsed);
            }
        }
    }

    
    private FireParticle recycleParticle(FireParticle aux) {
        
        aux.setLifespan((float) (STARTING_LIFESPAN + 0.1 * Math.random()));
        aux.getLocation().x = location.x - DISTANCE_OFFSET;
        aux.getLocation().y = 0;
        
        return aux;
    }
    
    public Vec2 getLocation() {
        return location;
    }

}
