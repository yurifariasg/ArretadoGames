package com.arretadogames.pilot.render.particlesystem;

import org.jbox2d.common.Vec2;

import android.graphics.Color;

import com.arretadogames.pilot.entities.Steppable;
import com.arretadogames.pilot.render.Renderable;
import com.arretadogames.pilot.render.opengl.GLCanvas;
import com.arretadogames.pilot.render.opengl.GLCircle;

public abstract class Particle implements Renderable, Steppable{
	private Vec2 location;
	private float lifespan;
	private int currentColor;
	private GLCircle circle;

	public Particle(Vec2 location, float lifespan){
		this.location = location;
		this.lifespan = lifespan;
		
		// Setting the Particle Configurations
//		this.acceleration = new Vec2( 0f, -0.05f );
//		this.velocity = new Vec2( ((float) Math.random()*1 ), (float) Math.random()*-2 );
//		this.currentColor = Color.RED;
//		this.circle = new GLCircle(8);
	}

	@Override
	public abstract void step(float timeElapsed);

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
			currentColor = Color.YELLOW;
		}else if (this.lifespan < 1.3f)
			currentColor = Color.rgb(255, 128, 0);			 //Orange
		else{
			currentColor = Color.RED;
		}
	}
	
	public void setLocation(Vec2 location){
		this.location = location;
	}
	
	public void setLifespan(float newLifespan){
		this.lifespan = newLifespan;
	}
	
	public abstract ParticleType getType();
}

