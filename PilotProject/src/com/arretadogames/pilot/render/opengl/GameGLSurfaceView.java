package com.arretadogames.pilot.render.opengl;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLSurfaceView;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

import com.arretadogames.pilot.GameActivity;
import com.arretadogames.pilot.config.DisplaySettings;
import com.arretadogames.pilot.game.Game;
import com.arretadogames.pilot.screens.InputEventHandler;

/**
 * GameGLSurfaceView class represents a GLSurfaceView specific for our Game,
 * which has operations to draw and perform the logic on the Game set into this
 * class
 */
public class GameGLSurfaceView extends GLSurfaceView implements GLSurfaceView.Renderer {
	
	private OpenGLCanvas gameCanvas;

	/**
	 * Creates a GameGLSurfaceView using the given context
	 * 
	 * @param context
	 *            Context to be used
	 */
	public GameGLSurfaceView(GameActivity activity) {
		super(activity);

		// Specifies the use of OpenGL 2.0
		setEGLContextClientVersion(2);

		// Specifies the Renderer and starts the Rendering Thread
		setRenderer(this);

		// Render the view only when there is a change in the drawing data
//		setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
		
		// Set OnTouchListener
		setOnTouchListener(activity);
	}

	@Override
	public void onDrawFrame(GL10 gl) {
		if (Game.getInstance() != null) {
			run();
		}

	}
	
	public void run() {
		/*
		 * This method runs the Game Loop and manage the time between each frame
		 */
		long frameEndedTime = getCurrentTime();
		long frameCurrentTime;
		
		gameCanvas = new OpenGLCanvas();

		frameCurrentTime = getCurrentTime();
		float elapsedTime = (frameCurrentTime - frameEndedTime)/1000.f;
		
		// Game Loop
		Game.getInstance().step(elapsedTime);
		
		if (gameCanvas.initiate()) { // If initiate was successful
			Game.getInstance().render(gameCanvas, elapsedTime);
			gameCanvas.flush();
		}
		// End Game Loop
		
		// Wait to complete 1/60 of a second
		long millisToWait = getTargetMilli(frameCurrentTime) - getCurrentTime();
		if (millisToWait > 0) {
			try {
				synchronized (this) {
					wait(millisToWait);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		frameEndedTime = frameCurrentTime;
	}
	
	private long getCurrentTime() {
		return System.nanoTime()/1000000;
	}
	
	private long getTargetMilli(long timeBefore) {
		return (long) (1000.0 / DisplaySettings.TARGET_FPS) + timeBefore;
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
	}

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
	}
	
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		Game.getInstance().input(new InputEventHandler(event));
		return super.onTouchEvent(event);
	}
}
