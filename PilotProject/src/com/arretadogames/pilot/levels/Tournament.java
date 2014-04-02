package com.arretadogames.pilot.levels;

public class Tournament {

//	this will be the six id's of the LevelTable of a tournament
	private int id;
	private int[] levels;
	private TournamentType typeName;
	private boolean enable;

	public Tournament(int id, TournamentType typeName) {
		this.id = id;
		levels = new int[6];
		this.typeName = typeName;
	}

	public int getId(){
		return this.id;
	}

	public int[] getIdsLevels(){
		return levels;
	}

	public TournamentType getTournamentType(){
		return typeName;
	}

	public boolean getEnable(){
		return this.enable;
	}

	public void setId(int id){
		this.id = id;
	}

	public void setIdsLevels(int[] levels){
		this.levels = levels;
	}

	public void setTournamentType(TournamentType type){
		typeName = type;
	}

	public void setEnable(boolean value) {
		this.enable = value;
	}
}
