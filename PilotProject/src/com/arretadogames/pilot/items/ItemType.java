package com.arretadogames.pilot.items;

public enum ItemType {
	DOUBLE_JUMP("Double Jump"),SUPER_JUMP("Super Jump"),SUPER_STRENGHT("Super Strenght")
	,SUPER_Velocity("Super Velocity");
	
	
	final String value;
	private ItemType(String ae){
		value = ae;
	}
	
	public String getValue(){
		return value;
	}
}
