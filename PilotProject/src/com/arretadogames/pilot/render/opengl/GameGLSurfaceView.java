package com.arretadogames.pilot.render.opengl;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.graphics.Color;
import android.graphics.Paint;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.util.Log;
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
	
	// FPS Settings
	private float[] fpsBuffer;
	private int fpsCounter;
	private Paint fpsPaint;

	/**
	 * Creates a GameGLSurfaceView using the given context
	 * 
	 * @param context
	 *            Context to be used
	 */
	public GameGLSurfaceView(GameActivity activity) {
		super(activity);

		// Specifies the use of OpenGL 2.0
		setEGLContextClientVersion(1);

		// Specifies the Renderer and starts the Rendering Thread
		setRenderer(this);

		// Render the view only when there is a change in the drawing data
//		setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
		
		// Set OnTouchListener
		setOnTouchListener(activity);
		
		getHolder().setFixedSize((int) DisplaySettings.TARGET_WIDTH, (int) DisplaySettings.TARGET_HEIGHT);
		
		if (DisplaySettings.SHOW_FPS) {
			fpsBuffer = new float[DisplaySettings.FPS_AVG_BUFFER_SIZE];
			for (int i = 0 ; i < fpsBuffer.length ; i++) {
				fpsBuffer[i] = 0; // initialize all 0
			}
			fpsCounter = 0;
			
			fpsPaint = new Paint();
			fpsPaint.setColor(Color.RED);
			fpsPaint.setTextSize(0.5f);
		}
	}

	@Override
	public void onDrawFrame(GL10 gl) {
		if (DisplaySettings.DISPLAY_WIDTH == -1 || DisplaySettings.DISPLAY_HEIGHT == -1) {
			DisplaySettings.DISPLAY_WIDTH = getWidth();
			DisplaySettings.DISPLAY_HEIGHT = getHeight();
			DisplaySettings.WIDTH_RATIO = DisplaySettings.DISPLAY_WIDTH / DisplaySettings.TARGET_WIDTH;
			DisplaySettings.HEIGHT_RATIO = DisplaySettings.DISPLAY_HEIGHT / DisplaySettings.TARGET_HEIGHT;
		}
		
		if (Game.getInstance() != null) {
			run(gl);
		}

	}
	private long frameEndedTime;
	private long time;
	
	public void run(GL10 gl) {
		/*
		 * This method runs the Game Loop and manage the time between each frame
		 */
		if (frameEndedTime == 0)
			frameEndedTime = getCurrentTime();
		
		gameCanvas = new OpenGLCanvas(gl);

		long frameCurrentTime = getCurrentTime();
		float elapsedTime = (frameCurrentTime - frameEndedTime) / 1000f;
		
		if (DisplaySettings.PROFILE_SPEED)
			time = getCurrentTime();
		
		// Game Loop
		Game.getInstance().step(elapsedTime);
		
		if (DisplaySettings.PROFILE_SPEED) {
			Log.d("Profile", "Step Speed: " + (getCurrentTime() - time));
			time = getCurrentTime();
		}
		
		if (gameCanvas.initiate()) { // If initiate was successful
			Game.getInstance().render(gameCanvas, elapsedTime);
			gameCanvas.flush();
		}
		
		if (DisplaySettings.PROFILE_SPEED) {
			Log.d("Profile", "Render Speed: " + (getCurrentTime() - time));
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
		
		if (DisplaySettings.SHOW_FPS) {
			
			fpsBuffer[fpsCounter] = (1000f/(getCurrentTime() - frameCurrentTime));
			fpsCounter = ++fpsCounter % fpsBuffer.length;
			
			gameCanvas.drawText("FPS: " + getAverageFPS(), 30, 20, fpsPaint, true);
		}
		
		frameEndedTime = frameCurrentTime;
	}
	
	private int getAverageFPS() {
		float sum = 0;
		for (int i = 0 ; i < fpsBuffer.length ; i++) {
			sum += fpsBuffer[i];
		}
		return (int) (sum / fpsBuffer.length);
	}
	
	private long getCurrentTime() {
		return System.nanoTime()/1000000;
	}
	
	private long getTargetMilli(long timeBefore) {
		return (long) (1000.0 / DisplaySettings.TARGET_FPS) + timeBefore;
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		// Sets the current view port to the new size.
		gl.glViewport(0, 0, width, height);
		// Select the projection matrix
		gl.glMatrixMode(GL10.GL_PROJECTION);
		// Reset the projection matrix
		gl.glLoadIdentity();
		// Orthographic mode for 2d
		GLU.gluOrtho2D(gl, 0, width, -height, 0);
		// Select the modelview matrix
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		// Reset the modelview matrix
		gl.glLoadIdentity();
	}

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		// SETTINGS
		// Set the background color to black ( rgba ).
		gl.glClearColor(0.0f, 0.0f, 0.0f, 0.5f);

		// DRAWING SETUP
		// NOTES: As we are always drawing with textures and viewing our
		// elements from the same side all the time we can leave all these
		// settings on the whole time
		// Enable face culling.
		gl.glEnable(GL10.GL_CULL_FACE);
		// What faces to remove with the face culling.
		gl.glCullFace(GL10.GL_BACK);
		
		// Enable Transparency
		gl.glEnable(GL10.GL_BLEND);
		gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
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
