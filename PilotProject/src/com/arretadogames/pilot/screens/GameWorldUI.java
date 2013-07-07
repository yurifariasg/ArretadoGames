package com.arretadogames.pilot.screens;

import android.view.MotionEvent;

import com.arretadogames.pilot.R;
import com.arretadogames.pilot.entities.Player;
import com.arretadogames.pilot.entities.PlayerNumber;
import com.arretadogames.pilot.render.opengl.GLCanvas;
import com.arretadogames.pilot.world.GameWorld;

public class GameWorldUI extends GameScreen {
	
	private GameWorld gWorld;
	
	public GameWorldUI(GameWorld gameWorld) {
		this.gWorld = gameWorld;
	}

	@Override
	public void render(GLCanvas canvas, float timeElapsed) {
		canvas.drawBitmap(R.drawable.ui_buttons, 0, 380);
	}

	@Override
	public void step(float timeElapsed) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void input(InputEventHandler event) { // FIX : Detect several inputs
		
		int action = event.getAction() & MotionEvent.ACTION_MASK;
		if (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_POINTER_DOWN) {
			pressButtons(event.getX(), event.getY(), true);
		} else if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_POINTER_UP) {
			pressButtons(event.getX(), event.getY(), false);
		}
		
		
		
	}
	
	private void pressButtons(float x, float y, boolean pressed) {
		if (y > 380) {
			if (x < 200) {
				// Jump 1
				Player p = gWorld.getPlayers().get(PlayerNumber.ONE);
				if(p!=null) p.setJumping(pressed);
			} else if (x < 400) {
				// Act 1
				Player p = gWorld.getPlayers().get(PlayerNumber.ONE);
				if(p!=null) p.setAct(pressed);
			} else if (x < 600) {
				// Jump 2
				Player p = gWorld.getPlayers().get(PlayerNumber.TWO);
				if(p!=null) p.setJumping(pressed);
			} else {
				// Act 2
				Player p = gWorld.getPlayers().get(PlayerNumber.TWO);
				if(p!=null) p.setAct(pressed);
			}
		}
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
	}

}
