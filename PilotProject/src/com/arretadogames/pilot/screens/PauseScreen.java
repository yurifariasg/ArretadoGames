package com.arretadogames.pilot.screens;

import android.graphics.Bitmap;
import android.view.MotionEvent;
import android.widget.Toast;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenAccessor;
import aurelienribon.tweenengine.equations.Quart;

import com.arretadogames.pilot.GameActivity;
import com.arretadogames.pilot.R;
import com.arretadogames.pilot.game.Game;
import com.arretadogames.pilot.game.GameState;
import com.arretadogames.pilot.loading.ImageLoader;
import com.arretadogames.pilot.render.GameCanvas;
import com.arretadogames.pilot.ui.AnimationManager;
import com.arretadogames.pilot.ui.GameButtonListener;
import com.arretadogames.pilot.ui.TextImageButton;

public class PauseScreen extends GameScreen implements TweenAccessor<PauseScreen>, GameButtonListener {
	
	private static final int CONTINUE_BT = 1;
	private static final int OPTIONS_BT = 2;
	private static final int QUIT_BT = 3;
	
	
	private static final float PAUSE_MENU_SIZE = 277; // 277
	
	private boolean isHidden;
	
	private final float ARROW_WIDTH;
	
	private Bitmap background;
	private float currentBlackAlpha;
	private float currentWidth;
	
	private TextImageButton continueBt;
	private TextImageButton optionsBt;
	private TextImageButton quitBt;
	
	public PauseScreen() {
		isHidden = true;
		background = ImageLoader.loadImage(R.drawable.pause_menu_bg);
		ARROW_WIDTH = background.getWidth() - PAUSE_MENU_SIZE;
		currentWidth = ARROW_WIDTH;
		currentBlackAlpha = 0;
		
		continueBt = new TextImageButton(CONTINUE_BT, 0, 91, this,
				ImageLoader.loadImage(R.drawable.bt_pause_selected),
				null,
				"continue");
		
		optionsBt = new TextImageButton(OPTIONS_BT, 0, 146, this,
				ImageLoader.loadImage(R.drawable.bt_pause_selected),
				null,
				"options");
		
		quitBt = new TextImageButton(QUIT_BT, 0, 201, this,
				ImageLoader.loadImage(R.drawable.bt_pause_selected),
				null,
				"quit");
	}

	@Override
	public void render(GameCanvas canvas, float timeElapsed) {

		canvas.fillScreen(currentBlackAlpha, 0, 0, 0);
		canvas.drawBitmap(background, (800 - currentWidth), 1);
		
		
		if (!isHidden) {
			float buttonX = 800 - currentWidth + ARROW_WIDTH + 2;
			continueBt.setX(buttonX);
			continueBt.render(canvas, timeElapsed);
			optionsBt.setX(buttonX);
			optionsBt.render(canvas, timeElapsed);
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
			optionsBt.input(event);
			quitBt.input(event);
		}
		
	}
	
	private void show() {
		isHidden = false;
		Tween.to(this, 1, 0.5f).target(PAUSE_MENU_SIZE + ARROW_WIDTH).ease(Quart.OUT).start(AnimationManager.getInstance());
		Tween.to(this, 2, 0.5f).target(150f).start(AnimationManager.getInstance());
		
	}
	
	private void hide() {
		isHidden = true;
		Tween.to(this, 1, 0.5f).target(ARROW_WIDTH).ease(Quart.IN).start(AnimationManager.getInstance());
		Tween.to(this, 2, 0.5f).target(0f).start(AnimationManager.getInstance());
	}

	@Override
	public void onBackPressed() {
		if (isHidden)
			show();
		else
			hide();
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		
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
		
		switch (buttonId) {
		case CONTINUE_BT:
			hide();
			break;
		case OPTIONS_BT:
			Toast.makeText(GameActivity.getContext(), "Not Implemented YET!", Toast.LENGTH_SHORT).show();
			break;
		case QUIT_BT:
			Game.getInstance().switchState(GameState.MAIN_MENU);
			break;
		default:
			break;
		}
		
	}
}
