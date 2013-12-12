package com.arretadogames.pilot.ui;

import android.view.MotionEvent;

import com.arretadogames.pilot.render.opengl.GLCanvas;
import com.arretadogames.pilot.screens.InputEventHandler;

public abstract class Button {
	
	protected int id;
	protected float x, y;
	protected float width, height;
	protected GameButtonListener listener;
	protected boolean isSelected;
	
	public Button(int id, float x, float y, float width, float height, GameButtonListener listener) {
		this.id = id;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.listener = listener;
		isSelected = false;
	}
	
	public void setWidth(float width) {
		this.width = width;
	}
	
	public float getWidth() {
		return width;
	}
	
	public void setHeight(float height) {
		this.height = height;
	}
	
	public float getHeight() {
		return height;
	}
	
	public abstract void render(GLCanvas canvas, float timeElapsed);
	
	public boolean input(InputEventHandler event) {
		
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			if (pressed(event.getX(), event.getY())) {
				isSelected = true;
				return true;
			} else {
				isSelected = false;
			}
			break;
		case MotionEvent.ACTION_MOVE:
			if (pressed(event.getX(), event.getY())) {
				isSelected = true;
				return true;
			} else {
				isSelected = false;
			}
			break;
		case MotionEvent.ACTION_UP:
			if (pressed(event.getX(), event.getY())) {
				if (listener != null)
					listener.onClick(id);
				isSelected = false;
				return true;
			}
			isSelected = false;
			break;
		default:
			break;
		}
		return false;
	}
	
	protected boolean pressed(float x, float y) {
		if (x > this.x && x < this.x + width) {
			// Is on X Range
			if (y > this.y && y < this.y + height) {
				// Is on Y Range
				return true;
			}
		}
		return false;
	}
	
	public float getX() {
		return x;
	}
	
	public float getY() {
		return y;
	}
	
	public void setX(float x) {
		this.x = x;
	}
	
	public void setY(float y) {
		this.y = y;
	}
	
}