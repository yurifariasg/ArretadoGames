package com.arretadogames.pilot.screens;

import com.arretadogames.pilot.config.GameSettings;

import android.view.MotionEvent;

public class InputEventHandler {
	
	private MotionEvent motionEvent;

	public InputEventHandler(MotionEvent event) {
		this.motionEvent = event;
	}
	
	public float getX() {
		return motionEvent.getX(getIndex()) / GameSettings.WidthRatio;
	}
	
	public float getY() {
		return motionEvent.getY(getIndex()) / GameSettings.HeightRatio;
	}

	public int getAction() {
		return motionEvent.getAction();
	}
	
	public MotionEvent getEvent() {
		return motionEvent;
	}
	
	public float getX(int pointerIndex) {
		return motionEvent.getX(pointerIndex) / GameSettings.WidthRatio;
	}
	
	public float getY(int pointerIndex) {
		return motionEvent.getY(pointerIndex) / GameSettings.HeightRatio;
	}
	
	private int getIndex() {
		  int idx = (motionEvent.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
		  return idx;
	}

}
