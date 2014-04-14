package com.arretadogames.pilot.tournaments;

public enum TournamentType {
	
	DESERT("mirage"), SWAMP("victoria"), JUNGLE("cacau");
	
	private String name;
	
	TournamentType(String name) {
		this.name = name;
	}
	
	public String toString(){
		return this.name;
	}
}