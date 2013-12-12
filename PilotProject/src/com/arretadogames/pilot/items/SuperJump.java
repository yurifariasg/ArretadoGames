package com.arretadogames.pilot.items;

import com.arretadogames.pilot.R;
import com.arretadogames.pilot.entities.Player;

public class SuperJump implements Item {

	private float c;
	
	public SuperJump( float c ){
		this.c = c;
	}
	
	public SuperJump(){
		this(1f);
	}
	
	@Override
	public void applyEffect(Player p) {
		p.setJumpAceleration(p.getJumpAceleration()+c);
	}

	@Override
	public int getImage() {
		return R.drawable.it_superjump;
	}

}
