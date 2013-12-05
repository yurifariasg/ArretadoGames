package com.arretadogames.pilot.database.descriptors;

import com.arretadogames.pilot.MainActivity;

public abstract class StoreItemDescriptor {
	
	private String name;
	private String description;
	private StoreItemType type;
	private int iconId;

	public StoreItemDescriptor(String name, String description, String res_id, StoreItemType type) {
		this.name = name;
		this.description = description;
		this.type = type;
		
		MainActivity act = MainActivity.getActivity();
		this.iconId = act.getResources().getIdentifier(res_id, "drawable", act.getPackageName());
	}
	
	public int getIconId() {
		return iconId;
	}
	
	public StoreItemType getType() {
		return type;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}

}
