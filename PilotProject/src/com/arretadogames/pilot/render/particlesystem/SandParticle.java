package com.arretadogames.pilot.render.particlesystem;

import org.jbox2d.common.Vec2;
import android.graphics.Color;

import com.arretadogames.pilot.render.opengl.GLCanvas;
import com.arretadogames.pilot.render.opengl.GLCircle;

public class SandParticle extends Particle {

	private Vec2 location;
	private float lifespan;
	
	private Vec2 acceleration;
	private Vec2 velocity;
	private int currentColor;
	private GLCircle circle;

	public SandParticle(Vec2 location, float lifespan) {
		super(location, lifespan);		
		this.location = location;
		this.lifespan = lifespan;
		
		// Setting the Particle Configurations
		this.acceleration = new Vec2( 0f, -0.05f );
		this.velocity = new Vec2( generateNum((float) Math.random()), (float) Math.random()*-2 );
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
		if (this.lifespan <= 0.5f){
			currentColor = Color.rgb(204, 102, 0);
		}else if (this.lifespan < 1.1f)
			currentColor = Color.rgb(153, 76, 0);
		else{
			currentColor = Color.rgb(102, 51, 0);
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
		return ParticleType.SAND_PARTICLE;
	}
}

