package com.arretadogames.pilot.render.particlesystem;

import org.jbox2d.common.Vec2;

import android.graphics.Color;

import com.arretadogames.pilot.entities.Steppable;
import com.arretadogames.pilot.render.Renderable;
import com.arretadogames.pilot.render.opengl.GLCanvas;
import com.arretadogames.pilot.render.opengl.GLCircle;

public class Particle implements Renderable, Steppable{
	private Vec2 location;
	private Vec2 velocity;
	private Vec2 acceleration;
	private float lifespan;
	private int currentColor;
	
	private GLCircle circle;

	public Particle(Vec2 location, Vec2 acceleration, float velx, float vely, float lifespan){
		this.location = location;
		this.acceleration = acceleration;
		this.velocity = new Vec2((float) Math.random()*velx, -(float)Math.random()*vely);
		this.lifespan = lifespan;
		this.currentColor = Color.BLUE;
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
		if (isDead())
			circle.drawCircle(canvas, location.x, location.y, Color.RED, true);
		else
			circle.drawCircle(canvas, location.x, location.y, currentColor, true);
	}
	
	public boolean isDead(){
		return this.lifespan <= 0;
    }
	
	public void setColor(){
		if (this.lifespan < 1)
			currentColor = Color.RED;
		else if (this.lifespan < 1.4f)
			currentColor = Color.GREEN;
		else if (this.lifespan < 2)
			currentColor = Color.GRAY;
	}
	
	public void setLocation(Vec2 location){
		this.location = location;
	}
	
	public void setLifespan(float newLifespan){
		this.lifespan = newLifespan;
	}
}

