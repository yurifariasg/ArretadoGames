package com.arretadogames.pilot.levels;

public enum TournamentType {
	
	DESERT("Mirage"), SWAMP("Victoria"), JUNGLE("cacau");
	
	private String name;
	
	TournamentType(String name) {
		this.name = name;
	}
	
	public String toString(){
		return this.name;
	}
}
