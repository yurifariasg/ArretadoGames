package com.arretadogames.pilot.levels;

import com.arretadogames.pilot.entities.EntityType;

public class WaterDescriptor extends EntityDescriptor {

	private float width,height,density;
	public WaterDescriptor(float x, float y, EntityType type, float w, float h, float d) {
		super(x, y, type);
		setWidth(w);
		setHeight(h);
		setDensity(d);
	}
	public float getWidth() {
		return width;
	}
	public void setWidth(float width) {
		this.width = width;
	}
	public float getHeight() {
		return height;
	}
	public void setHeight(float height) {
		this.height = height;
	}
	public float getDensity() {
		return density;
	}
	public void setDensity(float density) {
		this.density = density;
	}

}
