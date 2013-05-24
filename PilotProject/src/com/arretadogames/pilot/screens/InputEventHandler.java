package com.arretadogames.pilot.screens;

import com.arretadogames.pilot.config.DisplaySettings;

import android.view.MotionEvent;

public class InputEventHandler {
	
	private MotionEvent motionEvent;

	public InputEventHandler(MotionEvent event) {
		this.motionEvent = event;
	}
	
	public float getX() {
		return motionEvent.getX(getIndex()) / DisplaySettings.WIDTH_RATIO;
	}
	
	public float getY() {
		return motionEvent.getY(getIndex()) / DisplaySettings.HEIGHT_RATIO;
	}

	public int getAction() {
		return motionEvent.getAction();
	}
	
	public MotionEvent getEvent() {
		return motionEvent;
	}
	
	public float getX(int pointerIndex) {
		return motionEvent.getX(pointerIndex) / DisplaySettings.WIDTH_RATIO;
	}
	
	public float getY(int pointerIndex) {
		return motionEvent.getY(pointerIndex) / DisplaySettings.HEIGHT_RATIO;
	}
	
	private int getIndex() {
		  int idx = (motionEvent.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
		  return idx;
	}

}
