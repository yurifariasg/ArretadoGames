package com.arretadogames.pilot.ui;

import android.view.MotionEvent;

import com.arretadogames.pilot.screens.InputEventHandler;

public class ToggleButton extends ImageButton {
	
	public ToggleButton(int id, float x, float y,
			int selectedImageId, int unselectedImageId) {
		super(id, x, y, null, selectedImageId, unselectedImageId);
	}
	
	public ToggleButton(int id, float x, float y, GameButtonListener listener,
			int selectedImageId, int unselectedImageId) {
		super(id, x, y, listener, selectedImageId, unselectedImageId);
	}
	
	@Override
	public boolean input(InputEventHandler event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_UP:
			if (pressed(event.getX(), event.getY())) {
				isSelected = !isSelected;
				if (listener != null)
					listener.onClick(id);
				return true;
			}
			break;
		default:
			break;
		}
		return false;
	}
	
	public boolean isToggled() {
		return isSelected;
	}
	
	public void setToggled(boolean isToggled) {
		isSelected = isToggled;
	}
}
