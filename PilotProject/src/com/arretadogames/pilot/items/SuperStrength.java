package com.arretadogames.pilot.items;

import com.arretadogames.pilot.R;
import com.arretadogames.pilot.entities.Player;

public class SuperStrength implements Item {

	private float c;
	
	public SuperStrength(float c){
		this.c = c;
	}
	
	public SuperStrength(){
		this(2f);
	}
	
	@Override
	public void applyEffect(Player p) {
		// TODO Auto-generated method stub

	}

	@Override
	public int getImage() {
		return R.drawable.it_strength;
	}

}
