package com.arretadogames.pilot.levels;


import com.arretadogames.pilot.entities.EntityType;

public class LianaDescriptor extends EntityDescriptor{
	
	private float x1;
	private float y1;

	public LianaDescriptor(float x, float y, float x1, float y1, float size, EntityType type) {
		super(x, y, type, size);
		this.x1 = x1;
		this.y1 = y1;
	}
	
	public float getX() {
		return super.getX();
	}
	
	public float getY() {
		return super.getY();
	}
	
	public float getX1() {
		return x1;
	}
	
	public float getY1() {
		return y1;
	}
	
	public EntityType getType(){
		return super.getType();
	}
	
}