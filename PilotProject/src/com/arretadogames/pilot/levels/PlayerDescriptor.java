package com.arretadogames.pilot.levels;

import com.arretadogames.pilot.entities.EntityType;
import com.arretadogames.pilot.entities.PlayerNumber;

public class PlayerDescriptor extends EntityDescriptor {

	private PlayerNumber playerNumber;
	
	public PlayerDescriptor(float x, float y, EntityType type, PlayerNumber playerNumber) {
		super(x, y, type);
		this.playerNumber = playerNumber;
	}
	
	public PlayerNumber getPlayerNumber() {
		return playerNumber;
	}

}
