package com.arretadogames.pilot.render;


public class SpriteState {
	
	private String name;
	
	private int[] keyframesIds;
	private float[] keyFrameDuration;
	
	private int currentKeyFrameIndex = -1;
	private float currentFrameTimeLeft = 0;
	
	public SpriteState(String name, int[] keyframesIds, float[] keyFrameDuration) {
		this.name = name;
		this.keyframesIds = keyframesIds;
		this.keyFrameDuration = keyFrameDuration;
		this.currentKeyFrameIndex = 0;
		this.currentFrameTimeLeft = keyFrameDuration[currentKeyFrameIndex];
	}
	
	public String getName() {
		return this.name;
	}
	
	public int getCurrentFrame(float timeElapsed) {
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
		return keyframesIds[currentKeyFrameIndex];
	}

	private void nextFrame() {
		currentKeyFrameIndex++;
		if (currentKeyFrameIndex >= keyframesIds.length)
			currentKeyFrameIndex = 0;
		currentFrameTimeLeft = keyFrameDuration[currentKeyFrameIndex];
	}
	
	public void release() { // This method is no longer needed
		for (int i = 0 ; i < keyframesIds.length ; i++)
			keyframesIds[i] = 0;
		keyframesIds = null;
	}
	
}
