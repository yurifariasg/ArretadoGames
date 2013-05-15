package com.arretadogames.pilot.levels;

import com.arretadogames.pilot.entities.EntityType;

public class EntityDescriptor {
	
	private float size;
	private EntityType type;
	private float x, y;
	
	public EntityDescriptor(float x, float y, EntityType type, float size) {
		this.x = x;
		this.y = y;
		this.type = type;
		this.size = size;
	}
	
	public EntityDescriptor(float x, float y, EntityType type) {
		this(x, y, type, 1f);
	}
	
	
	public float getSize() {
		return size;
	}
	
	public EntityType getType() {
		return type;
	}
	
	public float getX() {
		return x;
	}
	
	public float getY() {
		return y;
	}
}
