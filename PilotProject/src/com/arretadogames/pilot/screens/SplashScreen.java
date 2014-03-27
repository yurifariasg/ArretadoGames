package com.arretadogames.pilot.screens;

import android.graphics.Color;
import android.opengl.GLES11;
import android.view.MotionEvent;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenAccessor;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.equations.Back;

import com.arretadogames.pilot.R;
import com.arretadogames.pilot.config.GameSettings;
import com.arretadogames.pilot.game.Game;
import com.arretadogames.pilot.game.GameState;
import com.arretadogames.pilot.render.opengl.GLCanvas;
import com.arretadogames.pilot.ui.AnimationManager;

import javax.microedition.khronos.opengles.GL10;

public class SplashScreen extends GameScreen implements TweenAccessor<SplashScreen> {

	private static final int TWEEN_ZOOM = 1;
	private static final int TWEEN_ANGLE = 2;
	private static final int TWEEN_LOGO_ALPHA = 3;

	private boolean animationStarted;

	private float currentZoom;
	private float currentAngle;
	private int currentBitmapAlpha;
	private int logoId;

	private Timeline timeline;

	private float imageWidth;
	private float imageHeight;

	public SplashScreen() {
		animationStarted = false;
		logoId = R.drawable.logo;

		imageWidth = getDimension(R.dimen.logo_width);
		imageHeight = getDimension(R.dimen.logo_height);
	}

	@Override
	public void render(GLCanvas canvas, float timeElapsed) {
		canvas.setClearColor(255, 0, 0, 0);

		canvas.saveState();
		float centerX = GameSettings.TARGET_WIDTH / 2;
		float centerY = GameSettings.TARGET_HEIGHT / 2;
		canvas.translate(centerX, centerY);
		canvas.rotate(currentAngle);
		canvas.translate(-centerX, -centerY);

		canvas.saveState();
		canvas.scale(2, currentZoom, 400, 180 + (300 - 180) / 2);
		canvas.drawRect(0, 180, 800, 300, Color.WHITE);
		canvas.restoreState();
		
		// TODO: Remove these two GL-Specific methods, insert it inside Canvas to have same functionality
		GLES11.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		
		canvas.drawBitmap(logoId,
		        centerX - imageWidth / 2, centerY - imageHeight / 2,
		        imageWidth, imageHeight,
		        currentBitmapAlpha);

        GLES11.glBlendFunc(GL10.GL_ONE, GL10.GL_ONE_MINUS_SRC_ALPHA);


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
		if (event.getEvent().getAction() == MotionEvent.ACTION_DOWN) {
			// Jump Splash
			if (timeline != null) {
				timeline.pause();
				timeline.free();
				AnimationManager.getInstance().killTarget(timeline);
				startMainMenu();
			}
		}
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

		timeline = Timeline.createSequence()
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
				.push(Tween.to(this, TWEEN_LOGO_ALPHA, 0.6f)
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
		Game.getInstance().goTo(GameState.MAIN_MENU);
	}

}
