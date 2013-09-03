package com.arretadogames.pilot.render;

import com.arretadogames.pilot.entities.Entity;

public abstract class Watchable extends Entity{

	private boolean watch;
	
	public Watchable(float x, float y){
		super(x, y);
		watch = true;
	}
	
	public boolean isEnabled(){
		return watch == true;
	}
	
	public boolean isDisabled(){
		return watch == false;
	}
	
	public void enableThis(){
		watch = true;
	}
	
	public void disableThis(){
		watch = false;
	}
}
