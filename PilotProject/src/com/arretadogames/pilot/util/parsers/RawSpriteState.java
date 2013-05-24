package com.arretadogames.pilot.util.parsers;

public class RawSpriteState {
	
	private String name;
	private String[] keyFrames;
	private float[] durations;
	private int keyFramePointer;
	
	public RawSpriteState(String name, int keyFrameCapacity) {
		this.keyFrames = new String[keyFrameCapacity];
		this.durations = new float[keyFrameCapacity];
		this.keyFramePointer = 0;
		this.name = name;
	}
	
	public boolean addKeyFrame(String keyFrame, float duration) {
		
		if (keyFramePointer < keyFrame.length()) {
			
			keyFrames[keyFramePointer] = keyFrame;
			durations[keyFramePointer] = duration;
			keyFramePointer++;
			
			return true;
		}
		return false;
	}
	
	public String getName() {
		return this.name;
	}
	
	public String[] getKeyFrames() {
		return keyFrames;
	}

	public float[] getDurations() {
		return durations;
	}
	
	

}
