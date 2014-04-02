package com.arretadogames.pilot.levels;

import java.util.Arrays;
import java.util.List;

public class LevelDescriptor {
	
	private List<EntityDescriptor> entities;
	private GroundDescriptor groundDescriptor;
	private int id;
	private int theBest;
	private int secondBest;
	private int thirdBest;
	private boolean isEnabled;
	private float levelLength;
	
	public int[] getRecords() {
//		if (theBest == 0 && secondBest == 0 && thirdBest == 0)
//			return null;
		
		return new int[] {theBest, secondBest, thirdBest};
	}

	public void setRecords(int[] records) {
		System.out.println("Settings Records: " + Arrays.toString(records));
		this.theBest = records[0];
		this.secondBest = records[1];
		this.thirdBest = records[2];
	}
	
	public void setNewRecord(int coins, int num) {
		switch(num){
			case 0: 
				theBest = coins;
				break;
			case 1: 
				secondBest = coins;
				break;
			case 2: 
				thirdBest = coins;
				break;
			default:
				break;
		}
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

	public float getLevelLength() {
		return levelLength;
	}

	public void setLevelLength(float levelLength) {
		this.levelLength = levelLength;
	}
	
}
