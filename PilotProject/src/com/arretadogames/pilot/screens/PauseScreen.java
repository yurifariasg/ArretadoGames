package com.arretadogames.pilot.screens;

import android.graphics.Color;
import android.view.MotionEvent;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenAccessor;
import aurelienribon.tweenengine.equations.Quart;

import com.arretadogames.pilot.R;
import com.arretadogames.pilot.config.GameSettings;
import com.arretadogames.pilot.game.Game;
import com.arretadogames.pilot.game.GameState;
import com.arretadogames.pilot.loading.FontLoader;
import com.arretadogames.pilot.loading.FontLoader.FontTypeFace;
import com.arretadogames.pilot.loading.ImageLoader;
import com.arretadogames.pilot.render.opengl.GLCanvas;
import com.arretadogames.pilot.ui.AnimationManager;
import com.arretadogames.pilot.ui.GameButtonListener;
import com.arretadogames.pilot.ui.TextImageButton;

public class PauseScreen extends GameScreen implements TweenAccessor<PauseScreen>, GameButtonListener {
	
	private static final int CONTINUE_BT = 1;
	private static final int RESTART_BT = 2;
	private static final int QUIT_BT = 3;
	
	private static final float PAUSE_MENU_SIZE = 277;
	
	private boolean isHidden;
	
	private final float ARROW_WIDTH;
	
	private int backgroundId;
	private float currentBlackAlpha;
	private float currentWidth;
	
	private TextImageButton continueBt;
	private TextImageButton restartBt;
	private TextImageButton quitBt;
	
	public PauseScreen() {
		isHidden = true;
		backgroundId = R.drawable.pause_menu_bg;
		ARROW_WIDTH = ImageLoader.checkBitmapSize(R.drawable.pause_menu_bg)[0] - PAUSE_MENU_SIZE;
		currentWidth = ARROW_WIDTH;
		currentBlackAlpha = 0;
		
		continueBt = new TextImageButton(CONTINUE_BT, 0, 91, this,
				R.drawable.bt_pause_selected,
				0,
				"continue",
				FontLoader.getInstance().getFont(FontTypeFace.TRANSMETALS_STROKED), 1);
		
		restartBt = new TextImageButton(RESTART_BT, 0, 146, this,
				R.drawable.bt_pause_selected,
				0,
				"restart",
				FontLoader.getInstance().getFont(FontTypeFace.TRANSMETALS_STROKED), 1);
		
		quitBt = new TextImageButton(QUIT_BT, 0, 201, this,
				R.drawable.bt_pause_selected,
				0,
				"quit",
				FontLoader.getInstance().getFont(FontTypeFace.TRANSMETALS_STROKED), 1);
	}

	@Override
	public void render(GLCanvas canvas, float timeElapsed) {

		canvas.drawRect(0, 0, 800, 480, Color.argb((int)currentBlackAlpha, 0, 0, 0));
		
		canvas.drawBitmap(backgroundId, (800 - currentWidth), 1);
		
		if (!isHidden) {
			float buttonX = 800 - currentWidth + ARROW_WIDTH + 2;
			continueBt.setX(buttonX);
			continueBt.render(canvas, timeElapsed);
			restartBt.setX(buttonX);
			restartBt.render(canvas, timeElapsed);
			quitBt.setX(buttonX);
			quitBt.render(canvas, timeElapsed);
			
		}
		
	}

	@Override
	public void step(float timeElapsed) {
		// TODO Auto-generated method stub
	}

	@Override
	public void input(InputEventHandler event) {
		
		if (isHidden && event.getAction() == MotionEvent.ACTION_UP) {
			if (event.getY() <= 77 && event.getX() > 800 - ARROW_WIDTH) {
				show();
			}
		} else if (!isHidden) {
			continueBt.input(event);
			restartBt.input(event);
			quitBt.input(event);
			if (event.getX() < GameSettings.TARGET_WIDTH - PAUSE_MENU_SIZE + ARROW_WIDTH)
				hide();
			
		}
		
	}
	
	public void show() {
		isHidden = false;
		Tween.to(this, 1, 0.5f).target(PAUSE_MENU_SIZE + ARROW_WIDTH).ease(Quart.OUT).start(AnimationManager.getInstance());
		Tween.to(this, 2, 0.5f).target(80f).start(AnimationManager.getInstance());
		
	}
	
	public void hide() {
		isHidden = true;
		Tween.to(this, 1, 0.5f).target(ARROW_WIDTH).ease(Quart.OUT).start(AnimationManager.getInstance());
		Tween.to(this, 2, 0.5f).target(0f).start(AnimationManager.getInstance());
	}

	@Override
	public void onPause() {
	}

	public boolean isHidden() {
		return isHidden;
	}

	@Override
	public int getValues(PauseScreen pScreen, int type, float[] returnValues) {
		if (type == 1)
			returnValues[0] = pScreen.currentWidth;
		if (type == 2)
			returnValues[0] = pScreen.currentBlackAlpha;
		return 1;
	}

	@Override
	public void setValues(PauseScreen pScreen, int type, float[] newValues) {
		if (type == 1)
			pScreen.currentWidth = newValues[0];
		if (type == 2)
			pScreen.currentBlackAlpha = newValues[0];
	}

	@Override
	public void onClick(int buttonId) {
		// All functions here are ASYNCHRONOUS
		switch (buttonId) {
		case CONTINUE_BT:
			hide();
			break;
		case RESTART_BT:
			Game.getInstance().goTo(GameState.LEVEL_RESTART);
			break;
		case QUIT_BT:
			Game.getInstance().goTo(GameState.MAIN_MENU);
			break;
		default:
			break;
		}
		
	}
}
