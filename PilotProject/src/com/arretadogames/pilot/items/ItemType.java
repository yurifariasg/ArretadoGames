package com.arretadogames.pilot.items;

public enum ItemType {
	DOUBLE_JUMP("Double Jump","DOUBLE_JUMP"),SUPER_JUMP("Super Jump" , "SUPER_JUMP"),SUPER_STRENGHT("Super Strenght","SUPER_STRENGHT")
	,SUPER_VELOCITY("Super Velocity","SUPER_VELOCITY");
	
	
	final String value;
	final String code;
	private ItemType(String ae, String code){
		value = ae;
		this.code = code;
		
	}
	
	public String getCode(){
		return code;
	}
	public String getValue(){
		return value;
	}
}
