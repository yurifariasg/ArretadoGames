package com.arretadogames.pilot.items;

import com.arretadogames.pilot.R;
import com.arretadogames.pilot.entities.Player;

public class Velocity implements Item{

	private final float C;
	
	public Velocity(float c){
		this.C = c;
	}
	
	public Velocity(){
		this(1f);
	}
	
	@Override
	public void applyEffect(Player p) {
		p.setMaxRunVelocity(p.getMaxRunVelocity()+C);
	}

	@Override
	public int getImage() {
		return R.drawable.it_speed;
	}

}
