package com.arretadogames.pilot.entities;

public enum EntityType {
	
	BOX("Box"), FRUIT("Fruit"),	GROUND("Ground"), PLAYER("Player");
	
	private String name;
	
	EntityType(String name) {
		this.name = name;
	}
	
	public String toString(){
		return this.name();
	}
	
}
