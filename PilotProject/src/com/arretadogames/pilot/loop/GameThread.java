package com.arretadogames.pilot.loop;

import com.arretadogames.pilot.game.Game;
import com.arretadogames.pilot.render.GameCanvas;

import android.view.SurfaceHolder;

/**
 * GameThread object that will hold the main loop for the Game
 */
public class GameThread extends Thread implements Runnable {

	private SurfaceHolder surfaceHolder;
	private boolean mRun;
	private GameCanvas gameCanvas;
	private Game game;

	/**
	 * Creates a GameThread
	 * 
	 * @param sholder
	 *            SurfaceHolder from the surface which the game will be rendered
	 * @param spanel
	 *            RenderingSurface which the game will be rendered
	 */
	public GameThread() {
		this.game = new Game();
		mRun = true;
	}
	
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
		long frameEndedTime = getCurrentTime();
		long frameCurrentTime;
		gameCanvas = new GameCanvas(surfaceHolder);
		while (mRun) {
			frameCurrentTime = getCurrentTime();
			float elapsedTime = (frameCurrentTime - frameEndedTime)/1000.f;
			
			// Game Loop
			game.step(elapsedTime);
			
			gameCanvas.initiate();
			game.render(gameCanvas, elapsedTime);
			gameCanvas.flush();
			
			frameEndedTime = frameCurrentTime;
		}
	}
	
	private long getCurrentTime() {
		return System.nanoTime()/1000000;
	}
	
}