package com.arretadogames.pilot.database.descriptors;

public class RealStoreItemDescriptor extends StoreItemDescriptor {

	private String skuCode;
	private float price;

	public RealStoreItemDescriptor(String name, String description, String resId,
			String skuCode, float price) {
		super(name, description, resId, StoreItemType.REAL, false);
		
		this.skuCode = skuCode;
		this.price = price;
	}
	
	public float getPrice() {
		return price;
	}
	
	public String getSkuCode() {
		return skuCode;
	}

}
