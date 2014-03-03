package com.arretadogames.pilot.render.particlesystem;

import android.graphics.Color;
import android.graphics.RectF;
import android.opengl.GLES11;

import com.arretadogames.pilot.R;
import com.arretadogames.pilot.render.opengl.GLCanvas;
import com.arretadogames.pilot.util.Util;

import org.jbox2d.common.Vec2;

import javax.microedition.khronos.opengles.GL10;

public class FireParticle extends Particle {

	private Vec2 location;
	private float lifespan;
	private float initialLifespan;
	
	private Vec2 velocity;
	private int currentColor;

	private static final float BECOME_GREY_AFTER = 0.3f; // in percentage
	private static final float BECOME_YELLOW_AFTER = 0.1f; // in percentage
	
	private RectF dstRect = new RectF();
	
	private final float PARTICLE_RADIUS = 10;
	private final float MAX_SIZE = 50;
	private float currentSize;

	public FireParticle(Vec2 location, Vec2 velocity, float lifespan){
		super(location, lifespan);		
		this.location = location;
		this.lifespan = lifespan;
		this.initialLifespan = lifespan;
		
		// Setting the Particle Configurations
		this.velocity = velocity; //new Vec2( 0.00001f, (float) Math.random()*1f ); // Initial Direction
		this.velocity.normalize();
		this.velocity.mulLocal(1f / 20f); // Velocity
		this.currentColor = Color.RED;
	}

	@Override
	public void step(float timeElapsed) {
		if (!isDead()) {
//			velocity.addLocal(acceleration);
			if (velocity.y < 0)
				velocity.y = 0;
			location.addLocal(velocity);
		    lifespan -= timeElapsed;
		    
		    currentSize = PARTICLE_RADIUS + (MAX_SIZE - PARTICLE_RADIUS) * (1 - (lifespan / initialLifespan));
		    if (currentSize < PARTICLE_RADIUS)
		    	currentSize = PARTICLE_RADIUS;
		    
		    // Convert to pixels
		    currentSize *= GLCanvas.physicsRatio;
			setColor();
		}
	}

	@Override
	public void render(GLCanvas canvas, float timeElapsed) {
		if (!isDead()) {
			GLES11.glBlendFunc(GL10.GL_ONE, GL10.GL_ONE_MINUS_SRC_ALPHA);
			
			GLES11.glColor4f(
					Color.red(currentColor) / 255f,
					Color.green(currentColor) / 255f,
					Color.blue(currentColor) / 255f,
					Color.alpha(currentColor) / 255f);
			
			// Draw Based on Physics now...
			canvas.saveState();
			canvas.translatePhysics(location.x, location.y);
			dstRect.top = - currentSize / 25f;
			dstRect.bottom = + currentSize / 25f;
			dstRect.left = - currentSize / 25f;
			dstRect.right = + currentSize / 25f;
			canvas.drawBitmap(R.drawable.white_particle, dstRect);
			canvas.restoreState();
	
			GLES11.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
			
			GLES11.glColor4f(1, 1, 1, 1);
		}
	}
	
	public boolean isDead(){
		return this.lifespan <= 0;
    }
	
	public void setColor(){
	    // Lifespan starts high, and goes until it reaches 0 (the particle dies)
	    
	    float elapsedTime = initialLifespan - lifespan;
	    float percantage = (elapsedTime) / initialLifespan;
	    
	    if (percantage > BECOME_GREY_AFTER) {
			currentColor = Color.argb(100, 20, 20, 20);
		} else if (percantage > BECOME_YELLOW_AFTER) {
            currentColor = Util.interpolateColor(
                    Color.argb(255, 243, 229, 0), // Yellow ( Initial )
                    Color.argb(100, 20, 20, 20), // Grey ( Final )
                    elapsedTime / BECOME_GREY_AFTER);
		} else {
            currentColor = Util.interpolateColor(
                    Color.argb(255, 243, 69, 0), // Orange ( Initial )
                    Color.argb(255, 243, 229, 0), // Yellow ( Final )
                    (elapsedTime) / (BECOME_YELLOW_AFTER));
		}
	}
	
	public void setLocation(Vec2 location){
		this.location = location;
	}
	
	public Vec2 getLocation() {
		return location;
	}
	
	public void setLifespan(float newLifespan){
		this.lifespan = newLifespan;
		this.initialLifespan = newLifespan;
	}

	@Override
	public ParticleType getType() {
		return ParticleType.FIRE_PARTICLE;
	}
}

