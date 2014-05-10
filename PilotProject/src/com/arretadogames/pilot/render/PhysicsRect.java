package com.arretadogames.pilot.render;

import android.graphics.RectF;

/**
 * PhysicsRect is a rect container for measurements inside the PhysicsWorld<br>
 * All values stored here are given in meters.
 */
public class PhysicsRect extends RectF {

	private float angle;

	public PhysicsRect(float width, float height) {
		this(width, height, 0);
	}

	public PhysicsRect(float width, float height, float angle) {
		super(-width / 2f, -height / 2f, width / 2f, height / 2f);
		this.angle = angle;
	}
	
	public float getAngle() {
		return angle;
	}
	
	@Override
	public void offset(float dx, float dy) {
		super.offset(dx, dy);
	}
	
	@Override
	public PhysicsRect clone() {
		PhysicsRect newR = new PhysicsRect(0, 0);
		newR.set(this);
		return newR;
	}

}
