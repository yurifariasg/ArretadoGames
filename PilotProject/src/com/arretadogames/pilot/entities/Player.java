package com.arretadogames.pilot.entities;

import com.arretadogames.pilot.game.Game;
import com.arretadogames.pilot.game.GameState;
import com.arretadogames.pilot.render.Watchable;
import com.arretadogames.pilot.world.GameWorld;



public abstract class Player extends Watchable {
	
	private PlayerNumber playerNumber;
	private boolean hasFinished; /* Player has finished level */
	
	protected boolean jumpActive;
	protected boolean actActive;
	
	private int acquiredCoins;
	private int deathCount;
	private int timeFinished;

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
		if (!hasFinished() && hasFinished) // Was not finished before, now it is
			timeFinished = ((GameWorld)Game.getInstance().getScreen(GameState.RUNNING_GAME)).getTotalElapsedTime();
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
	
	@Override
	public void setDead(boolean isDead) {
		if (isAlive() && isDead) // Was alive, now is dead
			deathCount++;
		
		super.setDead(isDead);
		disableThis();
	}
	
	public int getDeathCount() {
		return deathCount;
	}
	
	public int getTimeFinished() {
		if (hasFinished())
			return timeFinished;
		return 0;
	}
	
	public void addCoins(int amount) {
		acquiredCoins += amount;
	}
	
	public void resetCoins() {
		acquiredCoins = 0;
	}
	
	public int getCoins() {
		return acquiredCoins;
	}
	
	public double getPercentageLeftToNextAct(){
		return 1;
	}
	
	public abstract int[] getWalkFrames();
	
	public abstract int[] getJumpFrames();
	
	public abstract int[] getActFrames();

	public abstract float[] getWalkFramesDuration();
	
	public abstract float[] getJumpFramesDuration();
	
	public abstract float[] getActFramesDuration();
	
}
