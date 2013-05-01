package com.arretadogames.pilot.render;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.SurfaceHolder;

/**
 * GameCanvas is the Canvas class which drawing operations on the Game will be
 * done
 */
public class GameCanvas {

	private SurfaceHolder surfaceHolder;
	private Canvas canvas;

	public GameCanvas(SurfaceHolder surfaceHolder) {
		this.surfaceHolder = surfaceHolder;
	}

	/**
	 * Initiates the GameCanvas
	 * 
	 * @return True - Initiate was successful<br>
	 *         False - Initiate failed
	 */
	public boolean initiate() {
		canvas = surfaceHolder.lockCanvas();
		return canvas != null;
	}

	/**
	 * Flushes draws to screen
	 */
	public void flush() {
		surfaceHolder.unlockCanvasAndPost(canvas);
		canvas = null;
	}

	/**
	 * Draws a rect at the given location
	 * 
	 * @param x
	 *            Top Left X Position
	 * @param y
	 *            Top Left Y Position
	 * @param x2
	 *            Bottom Right X Position
	 * @param y2
	 *            Bottom Right Y Position
	 */
	public void drawRect(int x, int y, int x2, int y2) {
		Paint p = new Paint();
		p.setColor(Color.RED);
		canvas.drawRect(new Rect(x, y, x2, y2), p);
	}

}
