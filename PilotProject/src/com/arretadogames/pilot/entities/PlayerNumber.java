package com.arretadogames.pilot.entities;

public enum PlayerNumber {
	ONE("One"), TWO("Two");
	
	private String id;
	private PlayerNumber(String id) {
		this.id = id;
	}
	
	@Override
	public String toString() {
		return id;
	}
}
