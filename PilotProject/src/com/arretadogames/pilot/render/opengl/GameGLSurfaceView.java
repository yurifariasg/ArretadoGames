package com.arretadogames.pilot.render.opengl;

import android.app.Activity;
import android.opengl.GLES11;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

import com.arretadogames.pilot.MainActivity;
import com.arretadogames.pilot.config.GameSettings;
import com.arretadogames.pilot.game.Game;
import com.arretadogames.pilot.loading.FontLoader;
import com.arretadogames.pilot.loading.FontLoader.FontTypeFace;
import com.arretadogames.pilot.loading.FontSpecification;
import com.arretadogames.pilot.screens.InputEventHandler;
import com.arretadogames.pilot.util.Profiler;
import com.arretadogames.pilot.util.Profiler.ProfileType;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * GameGLSurfaceView class represents a GLSurfaceView specific for our Game,
 * which has operations to draw and perform the logic on the Game set into this
 * class
 */
public class GameGLSurfaceView extends GLSurfaceView implements GLSurfaceView.Renderer {

	private GLCanvas gameCanvas;

	// FPS Settings
	private float[] fpsBuffer;
	private int fpsCounter;
	private FontSpecification fpsFont;
	private Activity activity;

	/**
	 * Creates a GameGLSurfaceView using the given context
	 *
	 * @param context
	 *            Context to be used
	 */
	public GameGLSurfaceView(MainActivity activity) {
		super(activity);
		this.activity = activity;
	}

	@Override
	public void onDrawFrame(GL10 gl) {
		if (GameSettings.DisplayWidth == -1 || GameSettings.DisplayHeight == -1) {
			GameSettings.DisplayWidth = getWidth();
			GameSettings.DisplayHeight = getHeight();
			GameSettings.WidthRatio = GameSettings.DisplayWidth / GameSettings.TARGET_WIDTH;
			GameSettings.HeightRatio = GameSettings.DisplayHeight / GameSettings.TARGET_HEIGHT;
		}

		if (Game.getInstance() != null) {
			run(gl);
		}

	}
	private long frameEndedTime;

	public void run(GL10 gl) {
		/*
		 * This method runs the Game Loop and manage the time between each frame
		 */
		if (frameEndedTime == 0)
			frameEndedTime = getCurrentTime();

		long frameCurrentTime = getCurrentTime();
		float elapsedTime = (frameCurrentTime - frameEndedTime) / 1000f;

		Profiler.initTick(ProfileType.BASIC);

		// Game Loop
		Game.getInstance().step(elapsedTime);

		Profiler.profileFromLastTick(ProfileType.BASIC, "Game Step Speed");
		Profiler.initTick(ProfileType.BASIC);

		gameCanvas.setGLInterface(gl);
		gameCanvas.initiate();
		Game.getInstance().render(gameCanvas, elapsedTime);

		Profiler.profileFromLastTick(ProfileType.BASIC, "Game Render Speed");

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

		if (GameSettings.SHOW_FPS) {
			fpsBuffer[fpsCounter] = (1000f/(getCurrentTime() - frameCurrentTime));
			fpsCounter = ++fpsCounter % fpsBuffer.length;
			gameCanvas.drawText("FPS: " + getAverageFPS(), 10, 20, fpsFont, 0.8f, false);
		}

		frameEndedTime = frameCurrentTime;

		int error = GLES11.glGetError();
		if (error != 0) {
			System.out.println("OpenGL Error: " + GLU.gluErrorString(error));
		}
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
		return (long) (1000.0 / GameSettings.TARGET_FPS) + timeBefore;
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		// Sets the current view port to the new size.
		GLES11.glViewport(0, 0, width, height);
		// Select the projection matrix
		GLES11.glMatrixMode(GL10.GL_PROJECTION);
		// Reset the projection matrix
		GLES11.glLoadIdentity();
		// Orthographic mode for 2d
		GLU.gluOrtho2D(gl, 0, width, -height, 0);
		// Select the modelview matrix
		GLES11.glMatrixMode(GL10.GL_MODELVIEW);
		// Reset the modelview matrix
		GLES11.glLoadIdentity();
	}

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		// SETTINGS
		// Set the background color to black ( rgba ).
		GLES11.glClearColor(0.0f, 0.0f, 0.0f, 0.5f);

		// DRAWING SETUP
		// NOTES: As we are always drawing with textures and viewing our
		// elements from the same side all the time we can leave all these
		// settings on the whole time
		// Enable face culling.
		GLES11.glEnable(GL10.GL_CULL_FACE);
		// What faces to remove with the face culling.
		GLES11.glCullFace(GL10.GL_BACK);

		// Enable Transparency
		GLES11.glEnable(GL10.GL_BLEND);
		GLES11.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		Game.getInstance().input(new InputEventHandler(event));
		return super.onTouchEvent(event);
	}

	public void init() {
		// Specifies the use of OpenGL 2.0
		setEGLContextClientVersion(1);

		// Specifies the Renderer and starts the Rendering Thread
		setRenderer(this);

		// Set OnTouchListener
		setOnTouchListener((MainActivity)activity);

		gameCanvas = new GLCanvas();

		getHolder().setFixedSize((int) GameSettings.TARGET_WIDTH, (int) GameSettings.TARGET_HEIGHT);

		if (GameSettings.SHOW_FPS) {
			fpsBuffer = new float[GameSettings.FPS_AVG_BUFFER_SIZE];
			for (int i = 0 ; i < fpsBuffer.length ; i++) {
				fpsBuffer[i] = 0; // initialize all 0
			}
			fpsCounter = 0;

			fpsFont = FontLoader.getInstance().getFont(FontTypeFace.TRANSMETALS);
		}
	}
}
