package com.arretadogames.pilot.render.opengl;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import com.arretadogames.pilot.game.Game;
import com.arretadogames.pilot.screens.InputEventHandler;
import com.arretadogames.pilot.util.Util;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.view.MotionEvent;

/**
 * GameGLSurfaceView class represents a GLSurfaceView specific for our Game,
 * which has operations to draw and perform the logic on the Game set into this
 * class
 */
public class GameGLSurfaceView extends GLSurfaceView implements
		GLSurfaceView.Renderer {

	private static final boolean PROFILE_SPEED = false;

	private Game game;

	private long lastFrameStartingTime;

	/**
	 * Creates a GameGLSurfaceView using the given context
	 * 
	 * @param context
	 *            Context to be used
	 */
	public GameGLSurfaceView(Context context) {
		super(context);

		// Specifies the use of OpenGL 2.0
		setEGLContextClientVersion(2);

		// Specifies the Renderer and starts the Rendering Thread
		setRenderer(this);

		// Render the view only when there is a change in the drawing data
		setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
	}

	@Override
	public void onDrawFrame(GL10 gl) {
		if (game != null) {

			long before = System.nanoTime();
			float frameTimeDifference = Util.convertToSeconds(before
					- lastFrameStartingTime);

			game.step(frameTimeDifference);
			if (PROFILE_SPEED)
				Log.i("Profiling", "Step Speed: "
						+ (System.nanoTime() - before));

			long beforeRender = System.nanoTime();
//			game.render(gl, frameTimeDifference);

			if (PROFILE_SPEED)
				Log.i("Profiling", "Render Speed: "
						+ (System.nanoTime() - beforeRender));

			requestRender();

			lastFrameStartingTime = System.nanoTime();
		}

	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		// TODO Set ViewPort

	}

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		// TODO Set Created-Specific Configurations

	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		game.input(new InputEventHandler(event));
		return super.onTouchEvent(event);
	}

	/**
	 * Sets the Game to be used when drawing and performing step
	 * 
	 * @param game
	 *            A Game
	 */
	public void setGame(Game game) {
		this.game = game;
	}

}
