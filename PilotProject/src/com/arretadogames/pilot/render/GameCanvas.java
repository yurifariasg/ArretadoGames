package com.arretadogames.pilot.render;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.view.SurfaceHolder;

/**
 * GameCanvas is the Canvas class which drawing operations on the Game will be
 * done
 */
public class GameCanvas {
	
	private final static int SCREEN_WIDTH = 800;
	private final static int SCREEN_HEIGHT = 480;
	private final static float BOX2D_RATIO = 50f;
	

	private SurfaceHolder surfaceHolder;
	private Canvas canvas;

	private Paint defaultPaint;
	private Paint debugPaint;

	public GameCanvas(SurfaceHolder surfaceHolder) {
		this.surfaceHolder = surfaceHolder;

		defaultPaint = new Paint();
		defaultPaint.setAntiAlias(true);

		debugPaint = new Paint();
		debugPaint.setColor(Color.RED);
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
	 * Draws a debugging rect at the given location
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
	public void drawDebugRect(int x, int y, int x2, int y2) {
		canvas.drawRect(new Rect(x, y, x2, y2), debugPaint);
	}
	
	public void drawPhysicsDebugRect(float centerX, float centerY, float sideLength) {
		drawPhysicsDebugRect(centerX, centerY, sideLength, Color.RED);
	}
	
	public void drawPhysicsDebugRect(float centerX, float centerY, float sideLength, int color) {
		sideLength /= 2;
		debugPaint.setColor(color);
		canvas.drawRect(new Rect(
				(int) ((centerX - sideLength) * BOX2D_RATIO),
				(int) (SCREEN_HEIGHT - (centerY + sideLength) * BOX2D_RATIO),
				(int) ((centerX + sideLength)  * BOX2D_RATIO),
				(int) (SCREEN_HEIGHT - (centerY - sideLength) * BOX2D_RATIO)),
				debugPaint);
	}

	/**
	 * Saves the current state of the GameCanvas<br>
	 * This should be done before rotating or translating operations
	 */
	public void saveState() {
		canvas.save(Canvas.MATRIX_SAVE_FLAG);
	}

	/**
	 * Restores the canvas to the last saved state<br>
	 * This should be done after performing rotating, translating and drawing
	 * operations
	 */
	public void restoreState() {
		canvas.restore();
	}

	/**
	 * Rotates the canvas on the given point by the given angle
	 * 
	 * @param angle
	 *            Rotate the amount of degrees
	 * @param point
	 *            Rotates on this given point
	 */
	public void rotate(float angle, Point point) {
		canvas.rotate(angle, point.x, point.y);
	}

	/**
	 * Draws the given bitmap on the given coordinates
	 * 
	 * @param bitmap
	 *            Bitmap to be drawn
	 * @param x
	 *            x coordinate
	 * @param y
	 *            y coordinate
	 */
	public void drawBitmap(Bitmap bitmap, float x, float y) {
		drawBitmap(bitmap, x, y, defaultPaint);
	}

	/**
	 * Draws the given bitmap on the given coordinates
	 * 
	 * @param bitmap
	 *            bitmap to be drawn
	 * @param x
	 *            x coordinate
	 * @param y
	 *            y coordinate
	 * @param paint
	 *            paint to be used when drawing
	 * 
	 */
	public void drawBitmap(Bitmap bitmap, float x, float y, Paint paint) {
		canvas.drawBitmap(bitmap, x, y, paint);
	}
}
