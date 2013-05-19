package com.arretadogames.pilot.loop;

import com.arretadogames.pilot.config.DisplaySettings;
import com.arretadogames.pilot.game.Game;
import com.arretadogames.pilot.render.GameCanvas;
import com.arretadogames.pilot.render.canvas.RenderingCanvas;

import android.util.Log;
import android.view.SurfaceHolder;

/**
 * GameThread object that will hold the main loop for the Game
 */
public class GameThread extends Thread {
	
	// Target FPS which the game will run
	private static final float TARGET_FPS = 60.0f;

	private SurfaceHolder surfaceHolder;
	private boolean mRun;
	private GameCanvas gameCanvas;
	private Game game;
	
	private long time;

	/**
	 * Creates a GameThread
	 * 
	 * @param sholder
	 *            SurfaceHolder from the surface which the game will be rendered
	 * @param spanel
	 *            RenderingSurface which the game will be rendered
	 */
	public GameThread() {
		mRun = true;
	}
	
	/**
	 * Sets the Game to be run on this Thread
	 * @param game
	 */
	public void setGame(Game game) {
		this.game = game;
	}
	
	/**
	 * Sets the SurfaceHolder for Drawing Operations with Canvas
	 * @param surfaceHolder
	 */
	public void setSurfaceHolder(SurfaceHolder surfaceHolder) {
		this.surfaceHolder = surfaceHolder;
	}

	/**
	 * Sets the game running
	 * 
	 * @param bRun
	 *            True - Game Runs<br>
	 *            False - Stops Game
	 */
	public void setRunning(boolean bRun) {
		mRun = bRun;
	}

	@Override
	public void run() {
		/*
		 * This method runs the Game Loop and manage the time between each frame
		 */
		if (game == null)
			Log.e("GameThread.run()", "Game is null");
		
		long frameEndedTime = getCurrentTime();
		long frameCurrentTime;
		gameCanvas = new RenderingCanvas(surfaceHolder);
		while (mRun) {
			frameCurrentTime = getCurrentTime();
			float elapsedTime;
			if (DisplaySettings.USE_DYNAMIC_TIME)
				elapsedTime = (frameCurrentTime - frameEndedTime)/1000.f;
			else
				elapsedTime = 1f / DisplaySettings.TARGET_FPS;
			
			// Game Loop
			if (DisplaySettings.PROFILE_SPEED) {
				time = getCurrentTime();
			}
			game.step(elapsedTime);
			
			if (DisplaySettings.PROFILE_SPEED) {
				Log.d("Profile", "Step Time: " + (getCurrentTime() - time));
				time = getCurrentTime();
			}
			
			if (gameCanvas.initiate()) { // If initiate was successful
				game.render(gameCanvas, elapsedTime);
				gameCanvas.flush();
			}
			
			if (DisplaySettings.PROFILE_SPEED) {
				Log.d("Profile", "Render Time: " + (getCurrentTime() - time));
				time = getCurrentTime();
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
	}
	
	private long getCurrentTime() {
		return System.nanoTime()/1000000;
	}
	
	private long getTargetMilli(long timeBefore) {
		return (long) (1000.0 / TARGET_FPS) + timeBefore;
	}
	
}