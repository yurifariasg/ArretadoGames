package com.arretadogames.pilot.entities;

public enum EntityType {
	
	BOX("box"), GROUND("ground"), PLAYER("player"), FIRE("fire"),
	PULLEY("pulley"), ONEWAY_WALL("oneway_wall"), FINALFLAG("finalflag"),
	COIN("coin"), FLUID("fluid"), BREAKABLE("breakable"), LIANA("liana"),
	SPIKE("spike"), WATER("water"), HOLE("hole"),
	TREE("tree"), SHRUB("shrub");
	
	private String name;
	
	EntityType(String name) {
		this.name = name;
	}
	
	public String toString(){
		return this.name;
	}
	
}
