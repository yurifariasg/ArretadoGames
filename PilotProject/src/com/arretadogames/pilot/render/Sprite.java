package com.arretadogames.pilot.render;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import com.arretadogames.pilot.loading.LoadableGLObject;
import com.arretadogames.pilot.loading.LoadableType;

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
	
	public int getCurrentFrame(float timeElapsed) {
		return spriteStates.get(currentState).getCurrentFrame(timeElapsed);
	}
	
	public void setAnimationState(String state) {
		this.currentState = state;
		if (spriteStates.get(currentState) != null)
			spriteStates.get(currentState).resetIfInfinite();
	}
	
	public void release() {
		for (String state : spriteStates.keySet()) {
			spriteStates.get(state).release();
		}
		spriteStates.clear();
	}

	public List<LoadableGLObject> getAllFrames() {
		List<LoadableGLObject> loadableObject = new ArrayList<LoadableGLObject>();
		for (SpriteState state : spriteStates.values()) {
			if (state != null) {
				int[] frameIds = state.getFrames();
				if (frameIds != null) {
					for (int frameId : state.getFrames()) {
						loadableObject.add(new LoadableGLObject(frameId, LoadableType.TEXTURE));
					}
				}
			}
		}
		return loadableObject;
	}

}
