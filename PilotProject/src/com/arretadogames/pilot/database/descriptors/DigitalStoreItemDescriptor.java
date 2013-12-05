package com.arretadogames.pilot.database.descriptors;

public class DigitalStoreItemDescriptor extends StoreItemDescriptor {
	
	private int value;

	public DigitalStoreItemDescriptor(String name, String description, String resId, int value) {
		super(name, description, resId, StoreItemType.DIGITAL);
		this.value = value;
	}
	
	public int getValue() {
		return value;
	}

}
