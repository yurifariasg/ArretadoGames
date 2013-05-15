package com.arretadogames.pilot.render;

import android.graphics.Bitmap;

public class SpriteState {
	
	private String name;
	
	private Bitmap[] keyframes;
	private float[] keyFrameDuration;
	
	private int currentKeyFrameIndex = -1;
	private float currentFrameTimeLeft = 0;
	
	public SpriteState(String name, Bitmap[] keyframes, float[] keyFrameDuration) {
		this.name = name;
		this.keyframes = keyframes;
		this.keyFrameDuration = keyFrameDuration;
		this.currentKeyFrameIndex = 0;
		this.currentFrameTimeLeft = keyFrameDuration[currentKeyFrameIndex];
	}
	
	public String getName() {
		return this.name;
	}
	
	public Bitmap getCurrentFrame(float timeElapsed) {
		if (keyFrameDuration[currentKeyFrameIndex] > 0) {
			float currentTimeElapsed = timeElapsed;
			while (currentTimeElapsed > 0){
				float timeElapsedBefore = currentTimeElapsed;
				currentTimeElapsed -= currentFrameTimeLeft;
				currentFrameTimeLeft -= timeElapsedBefore;
				if (currentFrameTimeLeft < 0) { // Frame Expired
					nextFrame();
					if (currentFrameTimeLeft == 0)
						break; // if frame is suppose to be infinite
				}
			}
		}
		return keyframes[currentKeyFrameIndex];
	}

	private void nextFrame() {
		currentKeyFrameIndex++;
		if (currentKeyFrameIndex >= keyframes.length)
			currentKeyFrameIndex = 0;
		currentFrameTimeLeft = keyFrameDuration[currentKeyFrameIndex];
	}
	
	public void release() {
		for (int i = 0 ; i < keyframes.length ; i++)
			keyframes[i].recycle();
		keyframes = null;
	}
	
}
