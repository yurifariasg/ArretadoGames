package com.arretadogames.pilot.screens;

import android.graphics.Bitmap;
import android.view.MotionEvent;

import com.arretadogames.pilot.R;
import com.arretadogames.pilot.entities.PlayerNumber;
import com.arretadogames.pilot.loading.ImageLoader;
import com.arretadogames.pilot.render.GameCanvas;
import com.arretadogames.pilot.world.GameWorld;

public class GameWorldUI extends GameScreen {
	
	private Bitmap buttonsBitmap;
	private GameWorld gWorld;
	
	public GameWorldUI(GameWorld gameWorld) {
		buttonsBitmap = ImageLoader.loadImage(R.drawable.ui_buttons);
		this.gWorld = gameWorld;
	}

	@Override
	public void render(GameCanvas canvas, float timeElapsed) {
		canvas.drawBitmap(buttonsBitmap, 0, 380);
	}

	@Override
	public void step(float timeElapsed) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void input(InputEventHandler event) {
		// TODO Auto-generated method stub
		if (event.getAction() == MotionEvent.ACTION_DOWN &&
				event.getY() > 380) {
			if (event.getX() < 200) {
				// Jump 1
				gWorld.jumpPlayer(PlayerNumber.ONE);
			} else if (event.getX() < 400) {
				// Act 1
				gWorld.actPlayer(PlayerNumber.ONE);
			} else if (event.getX() < 600) {
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
