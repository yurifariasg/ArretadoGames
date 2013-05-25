package com.arretadogames.pilot.entities;



public abstract class Player extends Entity {
	
	private PlayerNumber playerNumber;
	private boolean hasFinished; /* Player has finished level */
	
	protected boolean jumpActive;
	protected boolean actActive;

	public Player(float x, float y, PlayerNumber playerNumber) {
		super(x, y);
		this.playerNumber = playerNumber;
		hasFinished = false;
		jumpActive = false;
		actActive = false;
	}
	
	public PlayerNumber getNumber() {
		return playerNumber;
	}
	
	public void setFinished(boolean hasFinished) {
		this.hasFinished = hasFinished;
	}
	
	public boolean hasFinished() {
		return hasFinished;
	}

	@Override
	public EntityType getType() {
		return EntityType.PLAYER;
	}
	
//	public abstract void jump();
	public void setJumping(boolean isJumping) {
		this.jumpActive = isJumping;
	}
	
//	public abstract void act();
	public void setAct(boolean isAct) {
		this.actActive = isAct;
	}
	
	public abstract int[] getWalkFrames();
	
	public abstract int[] getJumpFrames();
	
	public abstract int[] getActFrames();

	public abstract float[] getWalkFramesDuration();
	
	public abstract float[] getJumpFramesDuration();
	
	public abstract float[] getActFramesDuration();
	
}
