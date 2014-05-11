package com.arretadogames.pilot.screens;

import android.graphics.Color;
import android.view.MotionEvent;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenAccessor;
import aurelienribon.tweenengine.equations.Quart;

import com.arretadogames.pilot.R;
import com.arretadogames.pilot.config.GameSettings;
import com.arretadogames.pilot.game.Game;
import com.arretadogames.pilot.game.GameMode;
import com.arretadogames.pilot.game.GameState;
import com.arretadogames.pilot.loading.FontLoader;
import com.arretadogames.pilot.loading.FontLoader.FontTypeFace;
import com.arretadogames.pilot.render.opengl.GLCanvas;
import com.arretadogames.pilot.tournaments.TournamentManager;
import com.arretadogames.pilot.ui.AnimationManager;
import com.arretadogames.pilot.ui.GameButtonListener;
import com.arretadogames.pilot.ui.TextImageButton;
import com.arretadogames.pilot.ui.ZoomImageButton;
import com.arretadogames.pilot.util.Assets;

public class PauseScreen extends GameScreen implements TweenAccessor<PauseScreen>, GameButtonListener {

	private static final int CONTINUE_BT = 1;
	private static final int RESTART_BT = 2;
	private static final int QUIT_BT = 3;
	
	private final float GAME_WIDTH = getDimension(R.dimen.screen_width);
	private final float GAME_HEIGHT = getDimension(R.dimen.screen_height);

	private final float PAUSE_MENU_WIDTH;

	private boolean isHidden;

	private final float ARROW_WIDTH;

	private int backgroundId;
	private float currentBlackAlpha;
	private float currentWidth;
	
	private ZoomImageButton continueBt;
	private ZoomImageButton restartBt;
	private ZoomImageButton quitBt;

	public PauseScreen() {
		isHidden = true;
		backgroundId = R.drawable.pause_menu_bg;
//		PAUSE_MENU_WIDTH = getDimension(R.dimen.pause_bg_width) - getDimension(R.dimen.pause_bg_extra_width);
		PAUSE_MENU_WIDTH = 800;
		ARROW_WIDTH = getDimension(R.dimen.pause_menu_arrow_width);// ImageLoader.checkBitmapSize(R.drawable.pause_menu_bg)[0] - PAUSE_MENU_SIZE;
		currentWidth = ARROW_WIDTH;
		currentBlackAlpha = 0;

		continueBt = new ZoomImageButton(CONTINUE_BT, 0, 150,
				getDimension(R.dimen.main_menu_button_size) + 50,
				getDimension(R.dimen.main_menu_button_size) + 50,
                this,
				R.drawable.continue_selected,
				R.drawable.continue_unselected);
		
		restartBt = new ZoomImageButton(RESTART_BT, 0, 160,
				getDimension(R.dimen.main_menu_button_size) + 30,
				getDimension(R.dimen.main_menu_button_size) + 30,
                this,
				R.drawable.restart_selected,
				R.drawable.restart_unselected);
		
		quitBt = new ZoomImageButton(QUIT_BT, 0, 160,
				getDimension(R.dimen.main_menu_button_size) + 30,
				getDimension(R.dimen.main_menu_button_size) + 30,
                this,
				R.drawable.menu_selected,
				R.drawable.menu_unselected);
	}

	@Override
	public void render(GLCanvas canvas, float timeElapsed) {

		canvas.drawRect(0, 0, 800, 480, Color.argb((int)currentBlackAlpha, 0, 0, 0));

//		canvas.drawBitmap(backgroundId, (800 - currentWidth), 1,
//		        getDimension(R.dimen.pause_bg_width), getDimension(R.dimen.pause_bg_height),
//		        getDimension(R.dimen.pause_bg_extra_width), 0);
		
//		Fnish		
//		canvas.drawBitmap(backgroundId, 0, 0, GAME_WIDTH, GAME_HEIGHT, 0,
//				getDimension(R.dimen.main_menu_bg_extra_height));
		
		
		canvas.drawBitmap(backgroundId, (800 - currentWidth), 1,
		        GAME_WIDTH, GAME_HEIGHT, 0,
		        getDimension(R.dimen.main_menu_bg_extra_height));

		if (!isHidden) {
			float buttonX = GameSettings.TARGET_WIDTH - currentWidth + ARROW_WIDTH + 8 + 140;
			restartBt.setX(buttonX);
			restartBt.render(canvas, timeElapsed);
			continueBt.setX(buttonX + 140);
			continueBt.render(canvas, timeElapsed);
			quitBt.setX(buttonX + 300 );
			quitBt.render(canvas, timeElapsed);

		}

	}

	@Override
	public void step(float timeElapsed) {
		// TODO Auto-generated method stub
	}

	@Override
	public void input(InputEventHandler event) {
		
		if(isHidden) {
			
		} else {
			continueBt.input(event);
			restartBt.input(event);
			quitBt.input(event);
		}
		
		
//		if (isHidden && event.getAction() == MotionEvent.ACTION_UP) {
//			if (event.getY() <= 77 && event.getX() > 800 - ARROW_WIDTH) {
//				show();
//			}
//		} else if (!isHidden) {
//			continueBt.input(event);
//			restartBt.input(event);
//			quitBt.input(event);
//			if (event.getX() < GameSettings.TARGET_WIDTH - PAUSE_MENU_WIDTH + ARROW_WIDTH)
//				hide();
//
//		}

	}

	public void show() {
		isHidden = false;
		
		Tween.to(this, 1, 0.5f).target(PAUSE_MENU_WIDTH).ease(Quart.OUT).start(AnimationManager.getInstance());
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
			if (Game.getInstance().getGameMode() == GameMode.TOURNAMENT)
				TournamentManager.getInstance().resetTournamentData();
			Assets.mainMenuMusic.play();
			Game.getInstance().goTo(GameState.MAIN_MENU);
			break;
		default:
			break;
		}

	}
}
