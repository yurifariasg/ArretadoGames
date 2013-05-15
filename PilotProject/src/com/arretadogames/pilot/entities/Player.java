package com.arretadogames.pilot.entities;

import android.graphics.Bitmap;


public abstract class Player extends Entity {
	
	private PlayerNumber playerNumber;

	public Player(float x, float y, PlayerNumber playerNumber) {
		super(x, y);
		this.playerNumber = playerNumber;
	}
	
	public PlayerNumber getNumber() {
		return playerNumber;
	}

	@Override
	public EntityType getType() {
		return EntityType.PLAYER;
	}
	
	public abstract void jump();
	
	public abstract void act();
	
	public abstract Bitmap[] getWalkFrames();
	
	public abstract Bitmap[] getJumpFrames();
	
	public abstract Bitmap[] getActFrames();

	public abstract float[] getWalkFramesDuration();
	
	public abstract float[] getJumpFramesDuration();
	
	public abstract float[] getActFramesDuration();
	
}
