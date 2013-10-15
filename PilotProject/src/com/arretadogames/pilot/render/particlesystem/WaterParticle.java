package com.arretadogames.pilot.render.particlesystem;

import org.jbox2d.common.Vec2;

import android.graphics.Color;

import com.arretadogames.pilot.render.opengl.GLCanvas;
import com.arretadogames.pilot.render.opengl.GLCircle;

public class WaterParticle extends Particle {

	private Vec2 location;
	private float lifespan;
	
	private Vec2 acceleration;
	private Vec2 velocity;
	private int currentColor;
	private GLCircle circle;

	public WaterParticle(Vec2 location, float lifespan) {
		super(location, lifespan);		
		this.location = location;
		this.lifespan = lifespan;
		
		// Setting the Particle Configurations
		this.acceleration = new Vec2( 0f, -0.01f );
		this.velocity = new Vec2( generateNum((float) Math.random())*-1, (float) Math.random()*-2 );
		this.currentColor = Color.RED;
		this.circle = new GLCircle(8);
	}

	@Override
	public void step(float timeElapsed) {
		velocity.addLocal(acceleration);
		location.addLocal(velocity);
	    lifespan -= timeElapsed;
	}

	@Override
	public void render(GLCanvas canvas, float timeElapsed) {
		setColor();
		circle.drawCircle(canvas, location.x, location.y, currentColor, 1, true);
	}
	
	public boolean isDead(){
		return this.lifespan <= 0;
    }
	
	public void setColor(){
		if (this.lifespan <= 0.7f){
			currentColor = Color.rgb(0, 255, 255);
		}else if (this.lifespan < 1.3f)
			currentColor = Color.rgb(0, 128, 255);
		else{
			currentColor = Color.BLUE;
		}
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
		return ParticleType.WATER_PARTICLE;
	}
}


