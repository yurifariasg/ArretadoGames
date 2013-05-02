package com.arretadogames.pilot.entities;

public enum EntityType {
	BOX ("box"), 
	FRUIT ("fruit");
	
	private String type;
	private EntityType(String type) {
		this.type = type;
	}
}
