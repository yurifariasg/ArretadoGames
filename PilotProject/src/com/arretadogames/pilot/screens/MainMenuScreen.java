package com.arretadogames.pilot.screens;

import android.graphics.Bitmap;
import android.view.MotionEvent;

import com.arretadogames.pilot.R;
import com.arretadogames.pilot.game.Game;
import com.arretadogames.pilot.game.GameState;
import com.arretadogames.pilot.loading.ImageLoader;
import com.arretadogames.pilot.render.GameCanvas;
import com.arretadogames.pilot.ui.GameButtonListener;
import com.arretadogames.pilot.ui.ImageButton;

public class MainMenuScreen extends GameScreen implements GameButtonListener {
	
	private static final int PLAY_BUTTON = 1;
	
	private Bitmap background;
	private ImageButton playButton;
	private Game game;
	
	public MainMenuScreen(Game game) {
		this.game = game;
		background = ImageLoader.loadImage(R.drawable.menu_background);
		playButton = new ImageButton(PLAY_BUTTON, 340, 210, this,
				ImageLoader.loadImage(R.drawable.bt_play_selected),
				ImageLoader.loadImage(R.drawable.bt_play_unselected));
	}

	@Override
	public void render(GameCanvas canvas, float timeElapsed) {
		canvas.drawBitmap(background, 0, 0);
		playButton.render(canvas, timeElapsed);
	}

	@Override
	public void step(float timeElapsed) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void input(MotionEvent event) {
		playButton.input(event);
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onClick(int buttonId) {
		switch (buttonId) {
		case PLAY_BUTTON:
			game.switchState(GameState.RUNNING_GAME);
			break;
		}
	}

}
