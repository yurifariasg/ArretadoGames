package com.arretadogames.pilot.entities;

public enum PlayerNumber {
	ONE("One", 1), TWO("Two", 2);
	
	private String id;
	private Integer value;
	private PlayerNumber(String id, Integer value) {
		this.id = id;
		this.value = value;
	}
	
	public Integer getValue(){
		return value;
	}
	
	@Override
	public String toString() {
		return id;
	}
}
