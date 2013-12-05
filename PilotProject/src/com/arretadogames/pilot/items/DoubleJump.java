package com.arretadogames.pilot.items;

import com.arretadogames.pilot.entities.Player;

public class DoubleJump implements Item{

	private int c;
	
	public DoubleJump(){
		this(1);
	}
	
	public DoubleJump( int c){
		this.c = c;
	}
	
	@Override
	public void applyEffect(Player p) {
		p.setMaxDoubleJumps(p.getMaxDoubleJumps()+c);
	}

}
