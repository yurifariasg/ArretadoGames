package com.arretadogames.pilot.entities;


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
	
}
