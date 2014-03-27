package com.arretadogames.pilot.screens;

import com.arretadogames.pilot.config.GameSettings;

import android.view.MotionEvent;

public class InputEventHandler {
	
	private MotionEvent motionEvent;
	private int offsetX, offsetY;

	public InputEventHandler(MotionEvent event) {
		this.motionEvent = event;
	}
	
	public void setMotionEvent(MotionEvent event) {
		this.motionEvent = event;
	}
	
	public float getX() {
		return (motionEvent.getX(getIndex()) / GameSettings.WidthRatio) + getOffsetX();
	}
	
	public float getY() {
		return (motionEvent.getY(getIndex()) / GameSettings.HeightRatio) + getOffsetY();
	}

	public int getAction() {
		return motionEvent.getAction();
	}
	
	public MotionEvent getEvent() {
		return motionEvent;
	}
	
	public float getX(int pointerIndex) {
		return (motionEvent.getX(pointerIndex) / GameSettings.WidthRatio) + getOffsetX();
	}
	
	public float getY(int pointerIndex) {
		return (motionEvent.getY(pointerIndex) / GameSettings.HeightRatio) + getOffsetY();
	}
	
	private int getIndex() {
		  int idx = (motionEvent.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
		  return idx;
	}

	public int getOffsetX() {
		return offsetX;
	}

	public void setOffsetX(int offsetX) {
		this.offsetX = offsetX;
	}

	public int getOffsetY() {
		return offsetY;
	}

	public void setOffsetY(int offsetY) {
		this.offsetY = offsetY;
	}
	
}
