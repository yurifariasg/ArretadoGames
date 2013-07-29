package com.arretadogames.pilot.levels;

import java.util.List;

public class LevelDescriptor {
	
	private List<EntityDescriptor> entities;
	private GroundDescriptor groundDescriptor;
	private int id;
	
	/* Id here means Index on the LEVEL_TABLE */
	public LevelDescriptor(int id /* TODO: Add other parameters */) {
		// Add Database data here
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
