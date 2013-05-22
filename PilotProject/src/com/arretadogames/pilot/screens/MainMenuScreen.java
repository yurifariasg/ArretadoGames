package com.arretadogames.pilot.screens;

import aurelienribon.tweenengine.TweenAccessor;

import com.arretadogames.pilot.R;
import com.arretadogames.pilot.config.DisplaySettings;
import com.arretadogames.pilot.game.Game;
import com.arretadogames.pilot.game.GameState;
import com.arretadogames.pilot.render.opengl.GLCanvas;
import com.arretadogames.pilot.ui.GameButtonListener;
import com.arretadogames.pilot.ui.ImageButton;
import com.arretadogames.pilot.ui.ZoomImageButton;

public class MainMenuScreen extends GameScreen implements GameButtonListener, TweenAccessor<MainMenuScreen> {
	
	private final static int ZOOM_PROPERTY = 1;
	private final static int BLACK_ALPHA_PROPERTY = 2;
	
	private static final int PLAY_BUTTON = 1;
	
	private ImageButton playButton;
	private Game game;
	
	private float currentBlackAlpha;
	private float currentZoom;
	
	public MainMenuScreen(Game game) {
		this.game = game;
		playButton = new ZoomImageButton(PLAY_BUTTON, 340, 210, this,
				R.drawable.bt_play_selected,
				R.drawable.bt_play_unselected);
		
		currentBlackAlpha = 0;
		currentZoom = 1f;
	}

	@Override
	public void render(GLCanvas canvas, float timeElapsed) {
		
		canvas.saveState();
		
		canvas.fillScreen(255, 0, 0, 0);
		
		canvas.scale(currentZoom, currentZoom, DisplaySettings.TARGET_WIDTH / 2, DisplaySettings.TARGET_HEIGHT / 2);
		
		canvas.drawBitmap(R.drawable.menu_background, 0, 0);
		playButton.render(canvas, timeElapsed);
		
		canvas.fillScreen(currentBlackAlpha, 0, 0, 0);
		
		canvas.restoreState();
	}

	@Override
	public void step(float timeElapsed) {
		// TODO Auto-generated method stub
	}

	@Override
	public void input(InputEventHandler event) {
		playButton.input(event);
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		System.exit(0);
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onClick(int buttonId) {
		switch (buttonId) {
		case PLAY_BUTTON:
//			Tween.to(this, ZOOM_PROPERTY, 0.7f).target(3f).ease(Back.IN).start(AnimationManager.getInstance());
//			Tween.to(this, BLACK_ALPHA_PROPERTY, 0.7f).target(255f).setCallback(new TweenCallback() {
//				
//				@Override
//				public void onEvent(int arg0, BaseTween<?> arg1) {
//					startGame();
//				}
//			}).start(AnimationManager.getInstance());
			startGame();
			break;
		}
	}
	
	private void startGame() {
		game.goTo(GameState.RUNNING_GAME);
		currentBlackAlpha = 0;
		currentZoom = 1;
	}

	@Override
	public int getValues(MainMenuScreen target, int tweenType, float[] returnValues) {
		if (tweenType == ZOOM_PROPERTY) {
			returnValues[0] = target.currentZoom;
		} else if (tweenType == BLACK_ALPHA_PROPERTY) {
			returnValues[0] = target.currentBlackAlpha;
		}
		return 1;
	}

	@Override
	public void setValues(MainMenuScreen target, int tweenType, float[] newValues) {
		if (tweenType == ZOOM_PROPERTY) {
			target.currentZoom = newValues[0];
		} else if (tweenType == BLACK_ALPHA_PROPERTY) {
			target.currentBlackAlpha = newValues[0];
		}
	}


}
