package com.arretadogames.pilot.entities;

public enum EntityType {
	
	BOX("box"), FRUIT("fruit"),	GROUND("ground"), PLAYER("player"), FIRE("fire"),
	FINALFLAG("finalflag"), COIN("coin");
	
	private String name;
	
	EntityType(String name) {
		this.name = name;
	}
	
	public String toString(){
		return this.name;
	}
	
}
