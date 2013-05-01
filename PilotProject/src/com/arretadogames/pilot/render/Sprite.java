package com.arretadogames.pilot.render;

import java.util.HashMap;

import android.graphics.Bitmap;

public class Sprite {
	
	private HashMap<String, SpriteState> spriteStates;
	private String currentState;
	
	public Sprite() {
		spriteStates = new HashMap<String, SpriteState>();
		currentState = "default";
	}

	public void addState(SpriteState spriteAnimation) {
		spriteStates.put(spriteAnimation.getName(), spriteAnimation);
	}
	
	public Bitmap getCurrentFrame(float timeElapsed) {
		return spriteStates.get(currentState).getCurrentFrame(timeElapsed);
	}
	
	public void setAnimationState(String state) {
		this.currentState = state;
	}
	
	public void release() {
		for (String state : spriteStates.keySet()) {
			spriteStates.get(state).release();
		}
		spriteStates.clear();
	}

}
