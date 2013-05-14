package com.arretadogames.pilot.screens;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenAccessor;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.equations.Back;

import com.arretadogames.pilot.R;
import com.arretadogames.pilot.config.DisplaySettings;
import com.arretadogames.pilot.game.Game;
import com.arretadogames.pilot.game.GameState;
import com.arretadogames.pilot.loading.ImageLoader;
import com.arretadogames.pilot.render.GameCanvas;
import com.arretadogames.pilot.ui.AnimationManager;

public class SplashScreen extends GameScreen implements TweenAccessor<SplashScreen> {
	
	private static final int TWEEN_ZOOM = 1;
	private static final int TWEEN_ANGLE = 2;
	
	private boolean animationStarted;
	
	private float currentZoom;
	private float currentAngle;
	private Bitmap logo;
	
	private Game game;
	
	public SplashScreen(Game game) {
		animationStarted = false;
		this.game = game;
		logo = ImageLoader.loadImage(R.drawable.logo);
	}
	
	
	
	@Override
	public void render(GameCanvas canvas, float timeElapsed) {
		// TODO Auto-generated method stub
		canvas.fillScreen(255, 0, 0, 0);
		
		canvas.saveState();
		float centerX = DisplaySettings.TARGET_WIDTH / 2;
		float centerY = DisplaySettings.TARGET_HEIGHT / 2;
		canvas.rotate(currentAngle, centerX, centerY);
		
		canvas.saveState();
		canvas.scale(2, currentZoom, centerX, centerY);
		canvas.drawRect(new Rect(0, 180, 800, 300), Color.argb(255, 255, 255, 255));
		canvas.drawBitmap(logo, centerX - logo.getWidth() / 2, centerY - logo.getHeight() / 2);
		
		canvas.restoreState();
		
		canvas.restoreState();
		
	}

	@Override
	public void step(float timeElapsed) {
		if (!animationStarted) {
			startAnimation();
		}
		// TODO Auto-generated method stub
		
	}


	@Override
	public void input(InputEventHandler event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		
	}
	

	private void startAnimation() {
		
		animationStarted = true;
		
		currentAngle = -45f;
		currentZoom = 0.001f;
		
		Tween angleTween = Tween.to(this, TWEEN_ANGLE, 1f)
				.target(0f);
		
		final Tween zoomTween2 = Tween.to(this, TWEEN_ZOOM, 1f)
				.target(0.001f).delay(1f).ease(Back.IN);
		zoomTween2.setCallback(new TweenCallback() {
					
					@Override
					public void onEvent(int type, BaseTween<?> source) {
						startMainMenu();
					}
				});
		
		Tween.to(this, TWEEN_ZOOM, 1f)
				.target(1.5f) // FIXME Zoom should be 1
				.ease(Back.INOUT)
				.setCallback(new TweenCallback() {
					
					@Override
					public void onEvent(int arg0, BaseTween<?> arg1) {
						zoomTween2.start(AnimationManager.getInstance());
					}
				}).start(AnimationManager.getInstance());
		
		angleTween.start(AnimationManager.getInstance());
	}



	@Override
	public int getValues(SplashScreen splash, int type, float[] returnValues) {
		switch (type) {
		case TWEEN_ANGLE:
			returnValues[0] = splash.currentAngle;
			break;
		case TWEEN_ZOOM:
			returnValues[0] = splash.currentZoom;
			break;
		default:
			break;
		}
		
		return 1;
	}



	@Override
	public void setValues(SplashScreen splash, int type, float[] newValues) {
		switch (type) {
		case TWEEN_ANGLE:
			splash.currentAngle = newValues[0];
			break;
		case TWEEN_ZOOM:
			splash.currentZoom = newValues[0];
			break;
		default:
			break;
		}
		
	}
	
	private void startMainMenu() {
		game.goTo(GameState.MAIN_MENU);
	}

}
