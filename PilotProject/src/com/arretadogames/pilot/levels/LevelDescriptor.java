package com.arretadogames.pilot.levels;

import java.util.List;

public class LevelDescriptor {
	
	private List<EntityDescriptor> entities;
	private GroundDescriptor groundDescriptor;
	private int id;
	private int bestCoins;
	private boolean isEnabled;
	
	public int getBestCoins() {
		return bestCoins;
	}

	public void setBestCoins(int bestCoins) {
		this.bestCoins = bestCoins;
	}

	public boolean isEnabled() {
		return isEnabled;
	}

	public void setEnabled(boolean isEnabled) {
		this.isEnabled = isEnabled;
	}

	/* Id here means Index on the LEVEL_TABLE */
	public LevelDescriptor(int id) {
		this.id = id;
	}
	
	public GroundDescriptor getGroundDescriptor() {
		return groundDescriptor;
	}
	
	public List<EntityDescriptor> getEntities() {
		return entities;
	}
	
	public void setData(List<EntityDescriptor> entities, GroundDescriptor groundDescriptor) {
		this.entities = entities;
		this.groundDescriptor = groundDescriptor;
	}
	
	public boolean isLoaded() {
		return entities != null && groundDescriptor != null;
	}

	public int getId() {
		return id;
	}
	
	public void load() {
		LevelManager.loadLevel(this);
	}
	
}
