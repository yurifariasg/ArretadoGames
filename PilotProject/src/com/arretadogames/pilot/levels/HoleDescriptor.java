package com.arretadogames.pilot.levels;


import com.arretadogames.pilot.entities.EntityType;

public class HoleDescriptor extends EntityDescriptor{
	
	private float x1;
	private float distance;

	public HoleDescriptor(float x, float distance) {
		super(x - distance / 2, 0, EntityType.HOLE);
		this.x1 = x  + distance / 2;
		this.distance = distance;
	}
	
	public float getX2() {
		return x1;
	}
	
	public EntityType getType(){
		return super.getType();
	}

	public float getDistance() {
		return this.distance;
	}
	
}