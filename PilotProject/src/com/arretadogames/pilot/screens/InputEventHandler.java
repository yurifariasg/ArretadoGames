package com.arretadogames.pilot.screens;

import com.arretadogames.pilot.config.DisplaySettings;

import android.view.MotionEvent;

public class InputEventHandler {
	
	private MotionEvent motionEvent;

	public InputEventHandler(MotionEvent event) {
		this.motionEvent = event;
	}
	
	public float getX() {
		return motionEvent.getX() / DisplaySettings.WIDTH_RATIO;
	}
	
	public float getY() {
		return motionEvent.getY() / DisplaySettings.HEIGHT_RATIO;
	}

	public int getAction() {
		return motionEvent.getAction();
	}

}
