package com.arretadogames.pilot.render.particlesystem;

import javax.microedition.khronos.opengles.GL10;

import org.jbox2d.common.Vec2;

import android.graphics.Color;
import android.graphics.RectF;
import android.opengl.GLES11;

import com.arretadogames.pilot.R;
import com.arretadogames.pilot.render.opengl.GLCanvas;
import com.arretadogames.pilot.render.opengl.GLCircle;
import com.arretadogames.pilot.util.Util;

public class FireParticle extends Particle{

	private Vec2 location;
	private float lifespan;
	private final float INITIAL_LIFESPAN;
	
	private Vec2 acceleration;
	private Vec2 velocity;
	private int currentColor;
	private GLCircle circle;
	
//	private PhysicsRect
	private RectF dstRect = new RectF();
	
	private final float PARTICLE_RADIUS = 25;
	private final float MAX_SIZE = 100;
	private float currentSize;

	public FireParticle(Vec2 location, float lifespan){
		super(location, lifespan);		
		this.location = location;
		this.lifespan = lifespan;
		this.INITIAL_LIFESPAN = lifespan;
		
		// Setting the Particle Configurations
		this.acceleration = new Vec2( 0f, -0.05f );
		this.velocity = new Vec2( 0/*generateNum((float) Math.random())*/, -1 + (float) Math.random()*-1 );
		this.currentColor = Color.RED;
		this.circle = new GLCircle(8);
	}

	@Override
	public void step(float timeElapsed) {
//		velocity.addLocal(acceleration);
		location.addLocal(velocity);
	    lifespan -= timeElapsed;
	    
	    currentSize = PARTICLE_RADIUS + (MAX_SIZE - PARTICLE_RADIUS) * (1 - (lifespan / INITIAL_LIFESPAN));
	    if (currentSize < PARTICLE_RADIUS)
	    	currentSize = PARTICLE_RADIUS;
	}

	@Override
	public void render(GLCanvas canvas, float timeElapsed) {
		setColor();
		
		// No culling of back faces
		GLES11.glDisable(GLES11.GL_CULL_FACE);
		
		GLES11.glBlendFunc(GL10.GL_ONE, GL10.GL_ONE_MINUS_SRC_ALPHA);
		
		GLES11.glColor4f(
				Color.red(currentColor) / 255f,
				Color.green(currentColor) / 255f,
				Color.blue(currentColor) / 255f,
				Color.alpha(currentColor) / 255f);

		dstRect.top = location.y - currentSize;
		dstRect.bottom = location.y + currentSize;
		dstRect.left = location.x - currentSize;
		dstRect.right = location.x + currentSize;
		
		canvas.drawBitmap(R.drawable.white_particle, dstRect);

		GLES11.glBlendFunc(GL10.GL_ONE, GL10.GL_ONE_MINUS_SRC_ALPHA);
		
		GLES11.glColor4f(1, 1, 1, 1);
		
		// No culling of back faces
		GLES11.glEnable(GLES11.GL_CULL_FACE);
	}
	
	public boolean isDead(){
		return this.lifespan <= 0;
    }
	
	public void setColor(){
		float percentage = lifespan / INITIAL_LIFESPAN;
		if (percentage <= 0)
			percentage = 0;
		
		if (lifespan < 1.5f) {
			currentColor = Util.interpolateColor(
					Color.argb(100, 20, 20, 20), // Grey
					Color.argb(255, 243, 229, 0), // Yellow ( Initial )
//					Color.argb(0, 76, 76, 76), // Grey
//					Color.argb(255, 76, 76, 76),
					lifespan / 1.5f);
		} else {
			currentColor = Util.interpolateColor(
					Color.argb(255, 243, 229, 0), // Yellow ( Final )
					Color.argb(255, 243, 69, 0), // Orange ( Initial )
					(lifespan - 1.5f) / (INITIAL_LIFESPAN - 1.5f));//(percentage - 0.3f) / 0.7f);
		}
//		}
//		currentColor = Color.argb(1, 255, 128, 0);		
//		System.out.println("LifeSpan: " + lifespan + " Color ALpha: " + Color.alpha(currentColor));
	}
	
	public void setLocation(Vec2 location){
		this.location = location;
	}
	
	public void setLifespan(float newLifespan){
		this.lifespan = newLifespan;
	}
	
	private float generateNum(float num){
		if (isOdd(num))
			num *= -1;
		return num;
	}
	
	private boolean isOdd(float numb){
		numb *= 10;
		int num = (int) numb;
		if (num%2 == 0)
			return false;
		return true;
	}

	@Override
	public ParticleType getType() {
		return ParticleType.FIRE_PARTICLE;
	}
}

