package com.arretadogames.pilot.ui;

import aurelienribon.tweenengine.TweenManager;

public class AnimationManager extends TweenManager {
	
	private static AnimationManager manager;
	
	private AnimationManager() {
	}
	
	public static AnimationManager getInstance() {
		if (manager == null)
			manager = new AnimationManager();
		return manager;
	}

}
