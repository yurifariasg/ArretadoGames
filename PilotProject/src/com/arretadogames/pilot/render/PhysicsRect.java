package com.arretadogames.pilot.render;

import android.graphics.RectF;

public class PhysicsRect extends RectF {
	
	private float angle;
	
	public PhysicsRect(float width, float height) {
		this(width, height, 0);
	}

	public PhysicsRect(float width, float height, float angle) {
		super(- width / 2f, - height / 2f, width / 2f, height / 2f);
		this.angle = angle;
	}
	
	public float getAngle() {
		return angle;
	}

}
