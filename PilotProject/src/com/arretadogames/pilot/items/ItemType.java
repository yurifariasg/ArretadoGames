package com.arretadogames.pilot.items;

public enum ItemType {
    Mine("Mine"), Coconut("Coconut"), WaterWalk("Water Walk");
	
	private final String name;
	
	private ItemType(String name){
		this.name = name;
	}
	
	public String getName(){
		return this.name;
	}
	
	public static ItemType parse(String name) {
	    for (ItemType itemType : ItemType.values()) {
	        if (itemType.getName().equals(name)) {
	            return itemType;
	        }
	    }
	    return null;
	}
}
