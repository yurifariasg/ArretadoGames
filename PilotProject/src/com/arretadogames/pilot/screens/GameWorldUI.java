package com.arretadogames.pilot.screens;

import android.view.MotionEvent;

import com.arretadogames.pilot.R;
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
			pressButtons(event.getX(), event.getY());
		}
		
	}
	
	private void pressButtons(float x, float y) {
		if (y > 380) {
			if (x < 200) {
				// Jump 1
				gWorld.jumpPlayer(PlayerNumber.ONE);
			} else if (x < 400) {
				// Act 1
				gWorld.actPlayer(PlayerNumber.ONE);
			} else if (x < 600) {
				// Jump 2
				gWorld.jumpPlayer(PlayerNumber.TWO);
			} else {
				// Act 2
				gWorld.actPlayer(PlayerNumber.TWO);
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
