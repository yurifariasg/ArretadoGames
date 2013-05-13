package com.arretadogames.pilot.screens;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Timeline;
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
	private static final int TWEEN_LOGO_ALPHA = 3;
	
	private boolean animationStarted;
	
	private float currentZoom;
	private float currentAngle;
	private int currentBitmapAlpha;
	private Bitmap logo;
	
	private Game game;
	private Paint paintBitmap;
	
	public SplashScreen(Game game) {
		animationStarted = false;
		this.game = game;
		logo = ImageLoader.loadImage(R.drawable.logo);
		paintBitmap = new Paint();
		paintBitmap.setAntiAlias(true);
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
		canvas.restoreState();

		paintBitmap.setAlpha(currentBitmapAlpha);
		canvas.drawBitmap(logo, centerX - logo.getWidth() / 2, centerY - logo.getHeight() / 2, paintBitmap);
		
		
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
		currentBitmapAlpha = 0;
		
		Timeline.createSequence()
		.beginParallel()
		.push(Tween.to(this, TWEEN_ANGLE, 1f)
				.target(0f))
				
				.push(Tween.to(this, TWEEN_ZOOM, 1f)
				.target(1.5f) // FIXME Zoom should be 1
				.ease(Back.INOUT))
				.push(Tween.to(this, TWEEN_LOGO_ALPHA, 0.8f)
						.target(255)
						.delay(0.5f))
				.end()
				.pushPause(1)
				.beginParallel()
				.push(Tween.to(this, TWEEN_LOGO_ALPHA, 0.8f)
						.target(0))
				.push(Tween.to(this, TWEEN_ZOOM, 1f)
				.target(0.001f).ease(Back.IN))
				.end()
				
				.setCallback(new TweenCallback() {
					
					@Override
					public void onEvent(int type, BaseTween<?> source) {
						startMainMenu();
					}
				}).start(AnimationManager.getInstance());
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
		case TWEEN_LOGO_ALPHA:
			returnValues[0] = splash.currentBitmapAlpha;
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
		case TWEEN_LOGO_ALPHA:
			splash.currentBitmapAlpha = (int) newValues[0];
			break;
		default:
			break;
		}
		
	}
	
	private void startMainMenu() {
		game.switchState(GameState.MAIN_MENU);
	}

}
